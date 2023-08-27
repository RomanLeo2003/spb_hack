package com.my.golftrainer.presentation.screen.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.view.ViewGroup
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService


@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    executor: ExecutorService,
    isBackCamera: Boolean,
    videoCapture: VideoCapture<Recorder>,
    onImageAnalyzed: (image: ImageProxy) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder()
        .build()



    LaunchedEffect(isBackCamera) {
        cameraProvider.unbindAll()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(if (isBackCamera) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT)
            .build()

        val analysis = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also {
                it.setAnalyzer(executor) { image: ImageProxy ->
                    onImageAnalyzed(image)
                    image.close()
                }
            }

        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            analysis,
            videoCapture
        )
    }



    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            preview.setSurfaceProvider(previewView.surfaceProvider)
            previewView
        },
        modifier = modifier,
        update = {
            preview.setSurfaceProvider(it.surfaceProvider)
        }
    )
}


class MyImageAnalyzer(private val onImageAnalyzed: (tensor: Tensor) -> Unit) :
    ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        val bitmap = imageProxyToBitmap(imageProxy)
        val tensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )

        onImageAnalyzed(tensor)
        imageProxy.close()
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage =
            YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}


@Composable
fun OverlayView(
    modifier: Modifier = Modifier,
    landmarks: List<List<NormalizedLandmark>>,
    imageHeight: Int,
    imageWidth: Int,
    landmarkStrokeWidth: Float = 20f
) {
    val canvasSize = remember { mutableStateOf(IntSize(1, 1)) }

    val scaleFactor = canvasSize.value.height * 1f / imageHeight
    val offsetX = (canvasSize.value.width - imageWidth * scaleFactor) / 2
    val offsetY = (canvasSize.value.height - imageHeight * scaleFactor) / 2

    Canvas(modifier = modifier, onDraw = {
        canvasSize.value = IntSize(size.width.toInt(), size.height.toInt())

        if (landmarks.isNotEmpty())
            PoseLandmarker.POSE_LANDMARKS.forEach {
                drawLine(
                    Color(255, 255, 255, 179),
                    start = Offset(
                        landmarks[0][it.start()].x() * imageWidth * scaleFactor + offsetX,
                        landmarks[0][it.start()].y() * imageHeight * scaleFactor + offsetY
                    ),
                    end = Offset(
                        landmarks[0][it.end()].x() * imageWidth * scaleFactor + offsetX,
                        landmarks[0][it.end()].y() * imageHeight * scaleFactor + offsetY
                    ),
                    strokeWidth = landmarkStrokeWidth,
                    cap = StrokeCap.Round
                )
            }

        landmarks.forEach { landmark ->
            for (normalizedLandmark in landmark) {
                drawCircle(
                    Color(255, 255, 255, 179),
                    center = Offset(
                        normalizedLandmark.x() * imageWidth * scaleFactor + offsetX,
                        normalizedLandmark.y() * imageHeight * scaleFactor + offsetY
                    ),
                    radius = landmarkStrokeWidth / 1.2f
                )
                drawCircle(
                    Color(33, 150, 243, 179),
                    center = Offset(
                        normalizedLandmark.x() * imageWidth * scaleFactor + offsetX,
                        normalizedLandmark.y() * imageHeight * scaleFactor + offsetY
                    ),
                    radius = landmarkStrokeWidth / 2f
                )
            }
        }
    })
}

