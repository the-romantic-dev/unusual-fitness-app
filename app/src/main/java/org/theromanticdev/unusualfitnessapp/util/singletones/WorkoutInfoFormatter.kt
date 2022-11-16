package org.theromanticdev.unusualfitnessapp.util.singletones

import org.theromanticdev.unusualfitnessapp.R
import java.text.DateFormat
import java.util.*

object WorkoutInfoFormatter {

    fun formatTime(time: Long, style: Int): String {
        val date = Date(time * 1000L)
        return DateFormat.getTimeInstance(style).format(date)
    }

    fun formatDuration(time: Int): String {
        val hours = if (time / 3600 > 9) time / 3600 else "0${time / 3600}"
        val minutes = if (time / 60 % 60 > 9) time / 60 % 60 else "0${time / 60 % 60}"
        val seconds = if (time % 60 > 9) time % 60 else "0${time % 60}"
        return "$hours:$minutes:$seconds"
    }

    fun formatDate(time: Long, style: Int): String {
        val date = Date(time * 1000L)
        return DateFormat.getDateInstance(style).format(date)
    }

    fun formatDistanceToKm(meters: Int): String = "${meters / 100 / 10.0} km"

}