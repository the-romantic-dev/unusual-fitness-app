package org.theromanticdev.unusualfitnessapp.domain.models

data class TrainInfo(
    val type: Int,
    val startTime: Long,
    val finishTime: Long,
    val distance: Int,
    val route: List<Point>,
    val duration: Int
)