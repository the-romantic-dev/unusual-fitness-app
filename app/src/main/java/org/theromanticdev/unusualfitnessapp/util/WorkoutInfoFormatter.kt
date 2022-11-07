package org.theromanticdev.unusualfitnessapp.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.theromanticdev.unusualfitnessapp.R
import java.io.ByteArrayOutputStream
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

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }

    fun getResourceByWorkoutType(type: Int): Int {
        return when (type) {
            1 -> R.drawable.walking
            2 -> R.drawable.running
            else -> R.drawable.biking
        }
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap =
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}