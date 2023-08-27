package com.my.golftrainer.presentation.screen.analysisresult

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.my.golftrainer.presentation.CommonTopBar
import com.my.golftrainer.presentation.HeightSpacer
import com.my.golftrainer.presentation.utils.LocalPlaybackManager
import com.my.golftrainer.presentation.utils.PlaybackManager
import com.my.golftrainer.presentation.utils.log
import com.my.golftrainer.R
import com.my.golftrainer.presentation.WidthSpacer
import com.my.golftrainer.presentation.clickableWithoutAnimation
import com.my.golftrainer.presentation.theme.BoldText
import com.my.golftrainer.presentation.theme.ExtraSmallText
import com.my.golftrainer.presentation.theme.MediumRegularText
import com.my.golftrainer.presentation.theme.SmallShadowyText

@Composable
fun AnalysisResultScreen(
    vm: AnalysisResultViewModel = viewModel(),
    navigateBack: () -> Unit
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentState = vm.swingStates.windowed(2)
        .find { pos -> state.playbackPosition in pos.first().first until pos[1].first }
        ?.first()?.second

    LaunchedEffect(currentState) {
        log("currentSwingState $currentState")
    }


    val listener = object : PlaybackManager.PlaybackListener {
        override fun onPrepared() {
            vm.onEvent(AnalysisResultViewModel.Event.Prepared)
        }

        override fun onProgress(progress: Int) {
            vm.onEvent(AnalysisResultViewModel.Event.OnProgress(progress))
        }

        override fun onCompleted() {
            vm.onEvent(AnalysisResultViewModel.Event.Completed)
        }
    }

    val playbackManager = remember {
        PlaybackManager.Builder(context)
            .apply {
                this.uri = vm.uri
                this.listener = listener
                this.lifecycleOwner = lifecycleOwner
            }
            .build()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        CommonTopBar(title = "Результаты", navigateBack = navigateBack)
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(20.dp)) {
            BoldText(text = "9.8", fontSize = 30.sp, color = Color(230, 89, 80, 255))
            WidthSpacer(width = 5.dp)
            SmallShadowyText(text = "/10", modifier = Modifier.padding(bottom = 5.dp))
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .clickableWithoutAnimation {
                    if (state.playbackStatus == AnalysisResultViewModel.PlaybackStatus.Idle)
                        vm.onEvent(AnalysisResultViewModel.Event.PlayTapped)
                    else
                        vm.onEvent(AnalysisResultViewModel.Event.PauseTapped)
                }) {
            AndroidView(
                modifier = Modifier.height(500.dp),
                factory = { playbackManager.videoView })
        }

        HeightSpacer(height = 20.dp)

        Row(Modifier.fillMaxWidth()) {
            AnalysisResultViewModel.SwingState.values().take(4).forEach { swingState ->
                SwingStateItem(vm = vm, swingState = swingState, currentState = currentState)
            }
        }
        Row(Modifier.fillMaxWidth()) {
            AnalysisResultViewModel.SwingState.values().slice(4..7).forEach { swingState ->
                SwingStateItem(vm = vm, swingState = swingState, currentState = currentState)
            }
        }

        Column(Modifier.padding(20.dp)) {
            HeightSpacer(height = 10.dp)
            MediumRegularText(text = "Ошибки")
            HeightSpacer(height = 10.dp)
            SmallShadowyText(text = "Ошибое нет. Тренируйтесь в том же духе!")
            HeightSpacer(height = 20.dp)
            MediumRegularText(text = "Советы")
            HeightSpacer(height = 10.dp)
            SmallShadowyText(text = "Старайтесь держать голову в положениях P1 и P4 на одном уровне\n\nСтарайтесь держать угол между спиной и бедрами около 30 градусов, у вас он состовляет 25 градусов")

        }

    }

    LaunchedEffect(vm) {
        vm.effect.collect {
            when (it) {
                AnalysisResultViewModel.Effect.NavigateUp -> {
//                    navHostController.navigateTo(
//
//                        ScreenDestinations.Landing.route
//                    )
                }

                AnalysisResultViewModel.Effect.Pause -> playbackManager.pausePlayback()
                AnalysisResultViewModel.Effect.Play -> playbackManager.start(state.playbackPosition)
                    .also {
                        log("startet video from ${state.playbackPosition}")
                    }
            }
        }
    }

}


@Composable
fun RowScope.SwingStateItem(
    vm: AnalysisResultViewModel,
    swingState: AnalysisResultViewModel.SwingState,
    currentState: AnalysisResultViewModel.SwingState?,
) {
    Box(
        modifier = Modifier
            .clickableWithoutAnimation {
                val position = vm.swingStates.find { it.second == swingState }?.first
                log("newProgress $position")

                position?.let {
                    vm.updateProgress(it)
                }
            }
            .padding(5.dp)
            .border(
                if (swingState == currentState) 2.dp else 1.dp,
                if (swingState == currentState)
                    Color(255, 119, 119, 255)
                else Color(192, 192, 192, 255),
                RoundedCornerShape(15.dp)
            )
            .weight(1f)
            .aspectRatio(1.5f),
        contentAlignment = Alignment.Center
    ) {
        ExtraSmallText(text = swingState.title, modifier = Modifier.padding(10.dp))
    }
}