package com.my.golftrainer.presentation.screen.camera

import androidx.camera.core.CameraSelector
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.my.golftrainer.presentation.BackButton
import com.my.golftrainer.R
import com.my.golftrainer.presentation.clickableWithoutAnimation
import com.my.golftrainer.presentation.theme.MEDIUM_TEXT
import com.my.golftrainer.presentation.theme.SMALL_TEXT
import com.my.golftrainer.presentation.utils.log
import kotlinx.coroutines.delay
import kotlin.math.abs


val redColor = Color(221, 24, 24, 186)

@Composable
fun CameraScreen(
    vm: CameraViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToVideoProcessing: (String) -> Unit
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var isBackCamera = vm.cameraFacing == CameraSelector.LENS_FACING_BACK
    var timeInSeconds by remember { mutableStateOf(0L) }
    val humanInBounds = vm.poseLandmarkerResult.getOrNull(0)
        ?.all { (it.x() < 1 && it.x() >= 0) && (it.y() < 1 && it.y() >= 0) } == true

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            timeInSeconds += 1
        }
    }

    LaunchedEffect(vm.poseLandmarkerResult) {
        log("pose -> " + determinePose(vm.poseLandmarkerResult.getOrElse(0) { emptyList() }))
    }

    LaunchedEffect(vm.outputUri) {
        if (vm.outputUri.isNotEmpty())
            navigateToVideoProcessing(vm.outputUri)
    }

    LaunchedEffect(Unit) {
        vm.setPoseLandmarkerHelper(context)
//        vm.setPytorchModule(context)
    }

    Box {

        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            executor = vm.backgroundExecutor,
            isBackCamera = isBackCamera,
            videoCapture = vm.videoCapture,
        ) { image ->
            vm.detectPose(image)
//            vm.calculatePosition()
        }

        OverlayView(
            modifier = Modifier.fillMaxSize(),
            landmarks = vm.poseLandmarkerResult,
            imageHeight = vm.imageHeight,
            imageWidth = vm.imageWidth,
        )

        BackButton(
            tint = Color.White,
            onClick = navigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 25.dp, start = 20.dp)
        )




        if (isRecording)
            Box(
                Modifier
                    .padding(20.dp)
                    .background(redColor, RoundedCornerShape(10.dp))
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = String.format("%02d:%02d", timeInSeconds / 60, timeInSeconds % 60),
                    fontSize = MEDIUM_TEXT,
                    modifier = Modifier
                        .padding(vertical = 3.dp, horizontal = 20.dp),
                    color = Color.White
                )
            }


        CameraBottomBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            onRecordClick = {
                if (isRecording) {
                    vm.stopRecording()
                } else
                    vm.startRecording(context)
                isRecording = !isRecording
            },
            isRecording = isRecording,
            onChangeCameraClick = {
                vm.cameraFacing =
                    if (isBackCamera) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                isBackCamera = !isBackCamera
            },
            humanNotInBounds = !humanInBounds,
            notInBoundsText = if (vm.poseLandmarkerResult.isNotEmpty()) checkVisibility(
                vm.poseLandmarkerResult.first(), "side"
            ) else "Станьте полностью в кадр!",
            pose = determinePose(vm.poseLandmarkerResult.getOrElse(0) { emptyList() })
        )

    }
}

@Composable
fun CameraBottomBar(
    modifier: Modifier = Modifier,
    onRecordClick: () -> Unit,
    isRecording: Boolean,
    onChangeCameraClick: () -> Unit,
    humanNotInBounds: Boolean,
    notInBoundsText: String,
    pose: String
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (humanNotInBounds)
            Box(
                Modifier
                    .padding(20.dp)
                    .background(redColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = notInBoundsText,
                    fontSize = MEDIUM_TEXT,
                    modifier = Modifier
                        .padding(vertical = 3.dp, horizontal = 20.dp),
                    color = Color.White
                )
            }


        Text(
            text = "Pose: $pose",
            fontSize = SMALL_TEXT,
            modifier = Modifier
                .padding(vertical = 3.dp, horizontal = 20.dp)
                .align(Alignment.Start),
            color = Color.White
        )

        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            CircleButton(imageRes = R.drawable.help_outline, onClick = {})

            Surface(
                Modifier
                    .size(80.dp)
                    .clickableWithoutAnimation(onRecordClick),
                color = Color.Transparent,
                border = BorderStroke(2.dp, Color.White),
                shape = CircleShape
            ) {
                if (isRecording)
                    Surface(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(25.dp),
                        color = redColor,
                        shape = RoundedCornerShape(5.dp)
                    ) {

                    }
                else
                    Surface(
                        Modifier
                            .fillMaxSize()
                            .padding(7.dp),
                        color = redColor,
                        shape = CircleShape
                    ) {

                    }
            }

            CircleButton(imageRes = R.drawable.flip_camera, onClick = onChangeCameraClick)
        }
    }
}

@Composable
fun CircleButton(imageRes: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(55.dp)
            .border(
                BorderStroke(1.dp, Color(255, 255, 255, 87)),
                shape = CircleShape
            )
    ) {
        Icon(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

fun notVisiblePartOfBody(poseLandmarks: List<NormalizedLandmark>, pose: String): String {
    val landmarkData = mutableListOf<Int>()
    val resultList = mutableListOf<String>()

    poseLandmarks.forEach { landmark ->
        val x = landmark.x()
        val y = landmark.y()

        if ((x > 1 || x < 0) || (y > 1 || y < 0)) {
            landmarkData.add(0)
        } else {
            landmarkData.add(1)
        }
    }

    println(landmarkData)

    if (landmarkData.slice(14..14 step 2).none { it == 1 }) {
        resultList.add("Не видно правую руку")
    }
    if (landmarkData.slice(16..22 step 2).none { it == 1 }) {
        resultList.add("Не видно правую кисть")
    }

    if (landmarkData.slice(13..13 step 2).none { it == 1 }) {
        resultList.add("Не видно левую руку")
    }
    if (landmarkData.slice(17..21 step 2).none { it == 1 }) {
        resultList.add("Не видно левую кисть")
    }

    when (pose) {
        "side" -> {
            if (landmarkData.slice(26..26 step 2)
                    .all { it == 0 } && landmarkData.slice(28..32 step 2).none { it == 1 }
            ) {
                if (landmarkData.slice(25..25 step 2).none { it == 1 }) {
                    resultList.add("Не видно левую ногу")
                }
                if (landmarkData.slice(27..31 step 2).none { it == 1 }) {
                    resultList.add("Не видно левую стопу")
                }
            } else if (landmarkData.slice(25..25 step 2)
                    .all { it == 0 } && landmarkData.slice(27..31 step 2).none { it == 1 }
            ) {
                if (landmarkData.slice(26..26 step 2).none { it == 1 }) {
                    resultList.add("Не видно правую ногу")
                }
                if (landmarkData.slice(28..32 step 2).none { it == 1 }) {
                    resultList.add("Не видно правую стопу")
                }
            }
        }

        else -> {
            if (landmarkData.slice(26..26 step 2).none { it == 1 }) {
                resultList.add("Не видно правую ногу")
            }
            if (landmarkData.slice(28..32 step 2).none { it == 1 }) {
                resultList.add("Не видно правую стопу")
            }
            if (landmarkData.slice(25..25 step 2).none { it == 1 }) {
                resultList.add("Не видно левую ногу")
            }
            if (landmarkData.slice(27..31 step 2).none { it == 1 }) {
                resultList.add("Не видно левую стопу")
            }
        }
    }

    log("not visible ${resultList}")

    return if (resultList.size == 1) resultList.first() else "Станьте полностью в кадр"
}

fun checkVisibility(landmarks: List<NormalizedLandmark>, pose: String): String {
    val bodyParts = listOf(
        "Нос",
        "Левый глаз (внутренний)",
        "Левый глаз",
        "Левый глаз (внешний)",
        "Правый глаз (внутренний)",
        "Правый глаз",
        "Правый глаз (внешний)",
        "Левое ухо",
        "Правое ухо",
        "Рот (слева)",
        "Рот (справа)",
        "Левое плечо",
        "Правое плечо",
        "Левый локоть",
        "Правый локоть",
        "Левое запястье",
        "Правое запястье",
        "Левый мизинец",
        "Правый мизинец",
        "Левый указательный палец",
        "Правый указательный палец",
        "Левый большой палец",
        "Правый большой палец",
        "Левое бедро",
        "Правое бедро",
        "Левое колено",
        "Правое колено",
        "Левый лодыжка",
        "Правый лодыжка",
        "Левая пятка",
        "Правая пятка",
        "Левый палец ноги",
        "Правый палец ноги"
    )

    val invisibleParts = mutableListOf<String>()

    for ((index, landmark) in landmarks.withIndex()) {
        if (landmark.x() !in 0.0..1.0 || landmark.y() !in 0.0..1.0) {
            invisibleParts.add(bodyParts[index])
        }
    }

    val invisibleBodyRegions = mutableListOf<String>()

    if (setOf("Нос", "Левый глаз", "Правый глаз").any { it in invisibleParts }) {
        invisibleBodyRegions.add("Голова")
    }

    when (pose) {
        "front" -> {
            if (setOf(
                    "Левое плечо",
                    "Левый локоть",
                    "Левое запястье"
                ).any { it in invisibleParts }
            ) {
                invisibleBodyRegions.add("Левая рука")
            }
            if (setOf(
                    "Правое плечо",
                    "Правый локоть",
                    "Правое запястье"
                ).any { it in invisibleParts }
            ) {
                invisibleBodyRegions.add("Правая рука")
            }
            if (setOf(
                    "Левое бедро",
                    "Левое колено",
                    "Левый лодыжка"
                ).any { it in invisibleParts }
            ) {
                invisibleBodyRegions.add("Левая нога")
            }
            if (setOf(
                    "Правое бедро",
                    "Правое колено",
                    "Правый лодыжка"
                ).any { it in invisibleParts }
            ) {
                invisibleBodyRegions.add("Правая нога")
            }
        }

        "side" -> {
            if (setOf(
                    "Левое плечо",
                    "Левый локоть",
                    "Левое запястье"
                ).any { it in invisibleParts }
            ) {
                invisibleBodyRegions.add("Левая рука")
            }
            if (setOf(
                    "Правое плечо",
                    "Правый локоть",
                    "Правое запястье"
                ).any { it in invisibleParts }
            ) {
                invisibleBodyRegions.add("Правая рука")
            }
            if (setOf(
                    "Левое бедро",
                    "Левое колено",
                    "Левый лодыжка"
                ).any { it in invisibleParts }
            ) {
                invisibleBodyRegions.add("Левая нога")
            }
            if (setOf(
                    "Правое бедро",
                    "Правое колено",
                    "Правый лодыжка"
                ).any { it in invisibleParts }
            ) {
                invisibleBodyRegions.add("Правая нога")
            }
        }

        else -> throw IllegalArgumentException("Unknown pose: $pose")
    }

    return if (invisibleBodyRegions.isEmpty()) {
        "Все части тела видны в кадре"
    } else {
        "Не видны в кадре: ${invisibleBodyRegions.joinToString(", ")}"
    }
}

fun determinePose(landmarks: List<NormalizedLandmark>): String {
    if (landmarks.isEmpty()) return "none"
    val shoulderDistance = abs(landmarks[11].x() - landmarks[12].x())
    val hipDistance = abs(landmarks[23].x() - landmarks[24].x())

    log("shoulderDistance $shoulderDistance hipDistance $hipDistance")

    val threshold = 0.05 // Вы можете настроить это значение в зависимости от вашего набора данных

    return if (shoulderDistance < threshold && hipDistance < threshold) {
        "down_the_line"
    } else {
        "face_on"
    }
}