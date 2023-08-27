package com.my.golftrainer.presentation.screen.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.my.golftrainer.data.repository.SharedPreferencesRepository
import com.my.golftrainer.presentation.utils.FileManager
import com.my.golftrainer.presentation.utils.PoseLandmarkerHelper
import com.my.golftrainer.presentation.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.PyTorchAndroid
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.system.measureTimeMillis


@HiltViewModel
class CameraViewModel @Inject constructor(
    private val sp: SharedPreferencesRepository
) : ViewModel(), PoseLandmarkerHelper.LandmarkerListener {

    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    var backgroundExecutor = Executors.newSingleThreadExecutor()

    var cameraFacing by mutableStateOf(CameraSelector.LENS_FACING_BACK)

    var poseLandmarkerResult by mutableStateOf(listOf<List<NormalizedLandmark>>())
    var imageHeight by mutableStateOf(640)
    var imageWidth by mutableStateOf(480)
    var someNumber by mutableStateOf(12)
    var outputUri by mutableStateOf("")
    var isCalculatingPose by mutableStateOf(false)

    val recorder = Recorder.Builder().build()
    val videoCapture = VideoCapture.withOutput(recorder)
    private lateinit var activeRecording: Recording
    private lateinit var module: Module
    private lateinit var bitmap: Bitmap

    fun saveUri(uri: String) {
        sp.addNewRecordUri(uri)
    }

    fun setPytorchModule(context: Context) {
        module = PyTorchAndroid.loadModuleFromAsset(context.assets, "mobilenet_lite_last.ptl")
        bitmap = BitmapFactory.decodeStream(context.assets.open("down_the_line.jpeg"))
//        bitmap = BitmapFactory.decodeStream(context.assets.open("face_on.jpg"))

        viewModelScope.launch(Dispatchers.IO) {
            repeat(10) {
                calculatePosition()
                delay(1000)
            }
        }
    }

    private fun calculatePosition() {
        if (isCalculatingPose) return
        viewModelScope.launch(Dispatchers.Default) {
            isCalculatingPose = true
            val t = TensorImageUtils.bitmapToFloat32Tensor(
                bitmap,
                floatArrayOf(0f, 0f, 0f), floatArrayOf(1f, 1f, 1f),
            )
//            log("tensor bitmap ${t} -> ${t.dataAsFloatArray.take(5).toList()}")

            val output: Tensor = module.forward(IValue.from(t)).toTensor()
            log(
                "tensor ${
                    t.shape().toList()
                } result -> ${output.dataAsFloatArray.toList()} pose -> ${if (output.dataAsFloatArray.first() > output.dataAsFloatArray[1]) Pose.DownTheLine else Pose.FaceOn}"
            )
            isCalculatingPose = false
        }
    }


    fun setPoseLandmarkerHelper(context: Context) {

        backgroundExecutor.execute {
            poseLandmarkerHelper = PoseLandmarkerHelper(
                context = context,
                runningMode = RunningMode.LIVE_STREAM,
                poseLandmarkerHelperListener = this@CameraViewModel
            )
        }
    }

    override fun onError(error: String, errorCode: Int) {
        log("error $error, errorCode $errorCode")
    }

    override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
        poseLandmarkerResult = resultBundle.results.first().landmarks()

        imageWidth = resultBundle.inputImageWidth
        imageHeight = resultBundle.inputImageHeight
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            someNumber = (1..1000).random()
        }
    }

    fun detectPose(imageProxy: ImageProxy) {
        if (this@CameraViewModel::poseLandmarkerHelper.isInitialized) {
            poseLandmarkerHelper.detectLiveStream(
                imageProxy = imageProxy,
                isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
            )
        }
    }

    private val videoRecordingListener = Consumer<VideoRecordEvent> { event ->
        when (event) {
            is VideoRecordEvent.Finalize -> if (event.hasError()) {
                log("error ${event.cause}")
            } else {
                log("recordingCompleted ${event.outputResults.outputUri}")
                outputUri = event.outputResults.outputUri.toString()
                saveUri(outputUri)
            }

            is VideoRecordEvent.Status -> {
//                log("onProgress ${event.recordingStats.recordedDurationNanos}")
            }
        }
    }

    fun startRecording(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = FileManager(context).createFile("videos", "mp4")

            val outputOptions = FileOutputOptions.Builder(File(filePath)).build()
            activeRecording = videoCapture.output
                .prepareRecording(context, outputOptions)
                .start(ContextCompat.getMainExecutor(context), videoRecordingListener)
        }
    }

    fun stopRecording() {
        activeRecording.stop()
    }

    override fun onCleared() {
        super.onCleared()
        backgroundExecutor.shutdown()
    }

    enum class Pose {
        DownTheLine, FaceOn
    }
}

data class Landmark(val x: Float, val y: Float, val z: Float)
