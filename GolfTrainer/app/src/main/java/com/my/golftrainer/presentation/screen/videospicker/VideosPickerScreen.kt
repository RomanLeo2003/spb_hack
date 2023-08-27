package com.my.golftrainer.presentation.screen.videospicker

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.my.golftrainer.presentation.CommonSwitch
import com.my.golftrainer.presentation.CommonTopBar
import com.my.golftrainer.presentation.HeightSpacer
import com.my.golftrainer.presentation.theme.SmallShadowyText
import com.my.golftrainer.presentation.utils.formatTime
import com.my.golftrainer.presentation.utils.log

@Composable
fun VideosPickerScreen(
    vm: VideosPickerViewModel = hiltViewModel(), navigateBack: () -> Unit,
    navigateToVideoProcessing: (String) -> Unit
) {
    val context = LocalContext.current
    var isAnalysableVideosClicked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.getVideosFromDevice(context)
        log("videos size ${vm.videos.size}")
    }

    Column(Modifier.fillMaxWidth()) {
        CommonTopBar(title = "Галлерея", navigateBack = navigateBack)


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            SmallShadowyText(text = "Анализируемые")

            CommonSwitch(
                isChecked = isAnalysableVideosClicked,
                onClick = { isAnalysableVideosClicked = !isAnalysableVideosClicked })
        }

        HeightSpacer(height = 10.dp)
        VideoGrid(
            videos = if (isAnalysableVideosClicked) vm.videos.filter { it.duration < DurationThreshold } else vm.videos,
            onVideoClick = {
                log("uri -> ${it.uri.toString()}")
                vm.saveUri(it.uri.toString())
                navigateToVideoProcessing(it.uri.toString())
            })
    }
}

@Composable
fun VideoGrid(
    videos: List<VideosPickerViewModel.Video>,
    onVideoClick: (VideosPickerViewModel.Video) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
    ) {
        items(videos) { video ->
            VideoItem(video = video, onClick = onVideoClick)
        }
    }
}

@Composable
fun VideoItem(video: VideosPickerViewModel.Video, onClick: (VideosPickerViewModel.Video) -> Unit) {
    val isNotValid = video.duration > DurationThreshold
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable {
                if (isNotValid)
                    Toast
                        .makeText(
                            context,
                            "Выберите видео меньше ${DurationThreshold / 1000} секунд",
                            Toast.LENGTH_LONG
                        )
                        .show()
                else
                    onClick(video)
            }
            .padding(1.dp)
    ) {
        video.thumbnail?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            modifier = Modifier
                .padding(5.dp)
                .background(Color(0, 0, 0, 106), RoundedCornerShape(20.dp))
                .padding(vertical = 3.dp, horizontal = 10.dp)
                .align(Alignment.BottomEnd),
            text = formatTime(video.duration),
            color = Color.White,
            fontSize = 12.sp
        )
        if (isNotValid)
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(255, 255, 255, 124))
            )
    }
}

const val DurationThreshold = 15 * 1000