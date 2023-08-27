package com.my.golftrainer.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.my.golftrainer.R
import com.my.golftrainer.presentation.theme.MediumRegularText

@Composable
fun HeightSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
fun WidthSpacer(width: Dp) {
    Spacer(modifier = Modifier.width(width))
}


fun Modifier.clickableWithoutAnimation(onClick: () -> Unit) =
    composed {
        this.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    }

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    tint: Color = Color(48, 65, 86, 255),
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = modifier.size(18.dp)) {
        Icon(
            painter = painterResource(id = R.drawable.back),
            contentDescription = null,
            tint = tint
        )
    }
}

@Composable
fun CommonSwitch(modifier: Modifier = Modifier, isChecked: Boolean, onClick: (Boolean) -> Unit) {
    Switch(
        modifier = modifier,
        checked = isChecked,
        onCheckedChange = onClick,
        colors = SwitchDefaults.colors(
            uncheckedBorderColor = Color(151, 165, 172, 255),
            uncheckedTrackColor = Color(239, 239, 239, 255),
            uncheckedThumbColor = Color(151, 165, 172, 255),
            checkedTrackColor = Color(33, 150, 243, 255)
        )
    )
}

@Composable
fun CommonTopBar(
    title: String,
    navigateBack: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        BackButton(onClick = navigateBack)
        MediumRegularText(text = title, modifier = Modifier.align(Alignment.Center))
    }
}