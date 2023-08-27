package com.my.golftrainer.presentation.screen.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.my.golftrainer.presentation.HeightSpacer
import com.my.golftrainer.presentation.theme.MediumBoldText
import com.my.golftrainer.presentation.theme.SmallBoldText
import com.my.golftrainer.presentation.utils.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    vm: HistoryViewModel = hiltViewModel(),
    navigateToVideoProcessing: (String) -> Unit
) {

    val context = LocalContext.current
    LaunchedEffect(vm.recordUris) {
        vm.getThumbnailsFromUris(context, vm.recordUris)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        MediumBoldText(text = "История анализов свинга", modifier = Modifier.padding(20.dp))

        HeightSpacer(height = 10.dp)

        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(1.dp)) {
            items(vm.images) { videoDetails ->
                Card(
                    shape = RoundedCornerShape(20.dp), modifier = Modifier
                        .aspectRatio(1f)
                        .padding(5.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(247, 247, 247, 255)),
                    onClick = { navigateToVideoProcessing(videoDetails.uri) }
                ) {
                    Box() {
                        videoDetails.thumbnail?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Text(
                            modifier = Modifier
                                .padding(10.dp)
                                .background(Color(0, 0, 0, 106), RoundedCornerShape(15.dp))
                                .padding(vertical = 3.dp, horizontal = 10.dp)
                                .align(Alignment.BottomEnd),
                            text = formatTime(videoDetails.duration ?: 0),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                }
            }
        }
    }
}