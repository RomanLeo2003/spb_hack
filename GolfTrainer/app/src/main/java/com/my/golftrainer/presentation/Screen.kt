package com.my.golftrainer.presentation

import com.my.golftrainer.R


const val ARGUMENT_URI = "uri"

enum class Screen(val label: Int = 0, val icon: Int = 0) {
    //    Roadmap(label = R.string.roadmap, icon = R.drawable.roadmap),
    Home(label = R.string.home, icon = R.drawable.home),
    History(label = R.string.history, icon = R.drawable.list),
    Camera,
    VideosPicker,
    VideoProcessing,
    AnalysisResult
}

fun String.addArgs(name: String, value: String) = this.plus("?$name=$value")

fun String.addPathArgs(key: String) = this.plus("?$key={$key}")