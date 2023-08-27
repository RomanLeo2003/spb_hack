package com.my.golftrainer.presentation.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.my.golftrainer.R


val EXTRA_LARGE_TEXT = 30.sp
val LARGE_TEXT = 25.sp
val MEDIUM_TEXT = 20.sp
val SMALL_TEXT = 16.sp
val EXTRA_SMALL_TEXT = 12.sp


@Composable
fun BoldText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit,
    color: Color = Color(48, 65, 86, 255)
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontSize = fontSize,
        fontFamily = FontFamily(Font(R.font.rubik)),
        fontWeight = FontWeight.Bold
    )
}


@Composable
fun RegularText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit,
    color: Color = Color(48, 65, 86, 255)
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontSize = fontSize,
        fontFamily = FontFamily(Font(R.font.rubik)),
    )
}

@Composable
fun OnDarkText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit,
    color: Color = Color.White
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontSize = fontSize,
        fontFamily = FontFamily(Font(R.font.rubik)),
    )
}


@Composable
fun BoldShadowyText(modifier: Modifier = Modifier, text: String, fontSize: TextUnit) {
    BoldText(
        modifier = modifier,
        text = text,
        fontSize = fontSize,
        color = Color(56, 70, 79, 255)
    )
}

@Composable
fun ShadowyText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit,
    color: Color = Color(129, 129, 129, 255)
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontSize = fontSize,
        fontFamily = FontFamily(Font(R.font.rubik)),
    )
}


@Composable
fun LargeBoldText(modifier: Modifier = Modifier, text: String) {
    BoldText(
        modifier = modifier,
        text = text, fontSize = LARGE_TEXT
    )
}

@Composable
fun LargeRegularText(modifier: Modifier = Modifier, text: String) {
    RegularText(
        modifier = modifier,
        text = text, fontSize = LARGE_TEXT
    )
}

@Composable
fun ExtraLargeBoldText(modifier: Modifier = Modifier, text: String) {
    BoldText(
        modifier = modifier,
        text = text, fontSize = EXTRA_LARGE_TEXT
    )
}

@Composable
fun SmallBoldText(modifier: Modifier = Modifier, text: String) {
    BoldText(
        modifier = modifier,
        text = text, fontSize = SMALL_TEXT
    )
}



@Composable
fun LargeBoldShadowyText(modifier: Modifier = Modifier, text: String) {
    BoldShadowyText(
        modifier = modifier,
        text = text, fontSize = LARGE_TEXT
    )
}


@Composable
fun MediumBoldText(modifier: Modifier = Modifier, text: String) {
    BoldText(
        modifier = modifier,
        text = text, fontSize = MEDIUM_TEXT
    )
}

@Composable
fun MediumRegularText(modifier: Modifier = Modifier, text: String) {
    RegularText(
        modifier = modifier,
        text = text, fontSize = MEDIUM_TEXT
    )
}


@Composable
fun SmallBoldShadowyText(modifier: Modifier = Modifier, text: String) {
    BoldShadowyText(
        modifier = modifier,
        text = text, fontSize = SMALL_TEXT
    )
}

@Composable
fun MediumBoldShadowyText(modifier: Modifier = Modifier, text: String) {
    BoldShadowyText(
        modifier = modifier,
        text = text, fontSize = MEDIUM_TEXT
    )
}

@Composable
fun SmallBoldOnShadowyText(modifier: Modifier = Modifier, text: String) {
    BoldShadowyText(
        modifier = modifier,
        text = text, fontSize = SMALL_TEXT
    )
}


@Composable
fun SmallRegularText(modifier: Modifier = Modifier, text: String) {
    RegularText(
        modifier = modifier,
        text = text, fontSize = SMALL_TEXT
    )
}

@Composable
fun SmallShadowyText(modifier: Modifier = Modifier, text: String) {
    ShadowyText(
        modifier = modifier,
        text = text, fontSize = SMALL_TEXT
    )
}

@Composable
fun ExtraSmallBoldShadowyText(modifier: Modifier = Modifier, text: String) {
    BoldShadowyText(
        modifier = modifier,
        text = text, fontSize = EXTRA_SMALL_TEXT
    )
}

@Composable
fun ExtraSmallText(modifier: Modifier = Modifier, text: String) {
    RegularText(
        modifier = modifier,
        text = text, fontSize = EXTRA_SMALL_TEXT
    )
}

@Composable
fun ExtraSmallBoldText(modifier: Modifier = Modifier, text: String) {
    BoldText(
        modifier = modifier,
        text = text, fontSize = EXTRA_SMALL_TEXT
    )
}

@Composable
fun ExtraSmallOnDarkText(modifier: Modifier = Modifier, text: String) {
    OnDarkText(
        modifier = modifier,
        text = text, fontSize = EXTRA_SMALL_TEXT
    )
}
