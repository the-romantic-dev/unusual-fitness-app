package org.theromanticdev.unusualfitnessapp.domain.models

data class WorkoutInfo(
    val type: Int,
    val startTime: Long,
    val finishTime: Long,
    val distance: Int,
    val route: String,
    val duration: Int,
    val centerPoint: String,
    val snapshot: ByteArray,
    val zoom: Float,

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkoutInfo

        if (!snapshot.contentEquals(other.snapshot)) return false

        return true
    }

    override fun hashCode(): Int {
        return snapshot.contentHashCode()
    }
}