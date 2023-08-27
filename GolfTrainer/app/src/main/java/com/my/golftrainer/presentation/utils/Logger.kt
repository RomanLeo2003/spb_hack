package com.my.golftrainer.presentation.utils

import android.util.Log
import com.my.golftrainer.BuildConfig

fun log(message: String) {
    if (BuildConfig.DEBUG) Log.d("kek", message) else Unit
}

fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}