package com.my.golftrainer.presentation.screen.videoprocessing

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.my.golftrainer.presentation.ARGUMENT_URI
import com.my.golftrainer.presentation.utils.PoseLandmarkerHelper
import com.my.golftrainer.presentation.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


class VideoProcessingViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var progress by mutableStateOf(0f)
    var currentTip by mutableStateOf("")
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    var backgroundExecutor = Executors.newSingleThreadExecutor()
    val uri = Uri.parse(savedStateHandle.get<String>(ARGUMENT_URI))
    private val VIDEO_INTERVAL_MS = 300L
    var analysisFinished by mutableStateOf(false)


    init {


        log("uri - ${savedStateHandle.get<String>(ARGUMENT_URI)}")
        viewModelScope.launch {
            while (true) {
                currentTip = tipsList.random()
                delay(10000)
            }
        }

        viewModelScope.launch {

        }
    }


    fun runDetectionOnVideo(context: Context) {

        viewModelScope.launch(Dispatchers.IO) {
            (0..9999).forEach {
                progress = it.toFloat()
                delay(1)
            }
            analysisFinished = true
        }

        return
        backgroundExecutor.execute {
            poseLandmarkerHelper =
                PoseLandmarkerHelper(
                    context = context,
                    runningMode = RunningMode.VIDEO,
                )



            poseLandmarkerHelper.detectVideoFile(uri, VIDEO_INTERVAL_MS)
                ?.let { resultBundle ->
//                    displayVideoResult(resultBundle)


                    resultBundle.results.forEachIndexed { index, poseLandmarkerResult ->
                        val isValid = poseLandmarkerResult.landmarks().getOrNull(0)
                            ?.count { (it.x() < 1 && it.x() >= 0) && (it.y() < 1 && it.y() >= 0) }

                        log("${index * VIDEO_INTERVAL_MS / 1000}second valid -> $isValid result -> ${poseLandmarkerResult.landmarks()}")
                    }
                    analysisFinished = true
                }
                ?: run { log("Error running pose landmarker.") }

            poseLandmarkerHelper.clearPoseLandmarker()
        }
    }


    override fun onCleared() {
        super.onCleared()
        backgroundExecutor.shutdown()
    }


}

val tipsList = listOf(
    "Поставьте ноги на ширине плеч, согните колени и наклонитесь вперед, чтобы ваша спина была прямой.",
    "Выберите клюшку, соответствующую вашему росту и стилю игры.",
    "Упражнения для развития мощности и гибкости могут помочь вам улучшить свою игру.",
    "Практикуйтесь как можно больше.",
    "Сосредоточьтесь на мяче, а не на своих движениях.",
    "Не забывайте дышать правильно, чтобы сохранить спокойствие и сосредоточенность.",
    "Используйте правильную технику для удара по мячу, чтобы добиться наилучших результатов.",
    "Не забывайте о правильном питании и гидратации во время игры.",
    "Играйте с партнерами, которые могут помочь вам улучшить свою игру.",
    "Не забывайте о том, что гольф - это игра, и вы должны наслаждаться ею!",
    "Постарайтесь сохранять свою концентрацию на мяче во время игры.",
    "Не забывайте о правильной постановке рук и плеч при ударе.",
    "Постарайтесь не слишком сильно держать клюшку, чтобы избежать напряжения в руках и запястьях.",
    "Не забывайте о том, что гольф - это игра на открытом воздухе, поэтому не забудьте надеть защитные очки и крем от загара.",
    "Постарайтесь сохранять свою концентрацию на мяче во время игры.",
    "Не забывайте о правильной постановке рук и плеч при ударе.",
    "Постарайтесь не слишком сильно держать клюшку, чтобы избежать напряжения в руках и запястьях.",
    "Не забывайте о том, что гольф - это игра на открытом воздухе, поэтому не забудьте надеть защитные очки и крем от загара.",
    "Не забывайте о правильном выборе обуви. Обувь должна быть удобной и обеспечивать достаточную поддержку стопы.",
    "Не забывайте о правильном выборе одежды. Одежда должна быть удобной и не сковывать движений.",
    "Постарайтесь сохранять спокойствие и не допускать эмоциональных выбросов во время игры.",
    "Не забывайте отдыхать после долгих тренировок. Отдых поможет вашему телу восстановиться и избежать перенапряжения."
)