package com.my.golftrainer.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.my.golftrainer.presentation.HeightSpacer
import com.my.golftrainer.R
import com.my.golftrainer.presentation.WidthSpacer
import com.my.golftrainer.presentation.theme.MediumRegularText
import com.my.golftrainer.presentation.theme.SmallShadowyText

@Composable
fun HomeScreen(
    navigateToCamera: () -> Unit,
    navigateToVideosPicker: () -> Unit
) {

    Column(
        Modifier
            .fillMaxWidth()
            .padding(25.dp)
    ) {
        MediumRegularText(text = "Выберите метод для анализа")
        HeightSpacer(height = 30.dp)

        CommonCard(
            mainText = "Начать запись",
            secondaryText = "Запишите видео своего свинга для начала анализа",
            imageRes = R.drawable.golf_man,
            onClick = navigateToCamera
        )

        HeightSpacer(height = 20.dp)

        CommonCard(
            mainText = "Выбрать из галереи",
            secondaryText = "Выберите записанное видео из галереи",
            imageRes = R.drawable.golf_stick,
            onClick = navigateToVideosPicker
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommonCard(
    mainText: String,
    secondaryText: String,
    imageRes: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(247, 247, 247, 255)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            Column(Modifier.weight(1f)) {
                MediumRegularText(text = mainText)
                HeightSpacer(height = 5.dp)
                SmallShadowyText(text = secondaryText)
            }
            WidthSpacer(width = 10.dp)
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(70.dp)
            )
        }
    }
}