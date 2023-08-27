package com.my.golftrainer.presentation.screen.videoprocessing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.my.golftrainer.presentation.CommonTopBar
import com.my.golftrainer.presentation.HeightSpacer
import com.my.golftrainer.R
import com.my.golftrainer.presentation.theme.LargeRegularText
import com.my.golftrainer.presentation.theme.MediumRegularText
import com.my.golftrainer.presentation.theme.ShadowyText
import com.my.golftrainer.presentation.theme.SmallRegularText
import kotlin.math.roundToInt


@Composable
fun VideoProcessingScreen(
    vm: VideoProcessingViewModel = viewModel(),
    navigateBack: () -> Unit,
    navigateToAnalysisResult: (String) -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.golf_man_animation))
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.runDetectionOnVideo(context)
    }

    LaunchedEffect(vm.analysisFinished) {
        if (vm.analysisFinished){
            navigateToAnalysisResult(vm.uri.toString())
        }
    }


    Column() {
        CommonTopBar(title = "Анализ видео", navigateBack = navigateBack)

        Spacer(modifier = Modifier.weight(1f))

        Column(
            Modifier
                .fillMaxWidth()
                .padding(35.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                modifier = Modifier.size(240.dp),
                composition = composition,
                iterations = LottieConstants.IterateForever
            )
            HeightSpacer(height = 5.dp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .height(25.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .weight(1f),
                    progress = vm.progress / 1000,
                    color = Color(33, 150, 243, 255),
                    trackColor = Color(216, 216, 216, 255)
                )

                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.size(width = 60.dp, height = 40.dp)
                ) {
                    LargeRegularText(
                        text = "${vm.progress.roundToInt() / 100}%",
                    )
                }
            }
            HeightSpacer(height = 10.dp)
            ShadowyText(
                text = "Время анализа может розниться в зависимости от устройства или длительности видео",
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.weight(4f))


        Column(
            Modifier
                .fillMaxWidth()
                .background(Color(245, 245, 245, 255))
                .padding(20.dp)
        ) {
            MediumRegularText(text = "Совет:")
            HeightSpacer(height = 10.dp)
            SmallRegularText(text = vm.currentTip)
            HeightSpacer(height = 20.dp)
        }
    }

}