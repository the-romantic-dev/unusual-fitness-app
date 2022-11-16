package org.theromanticdev.unusualfitnessapp.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream

object DatabaseInfoConverter {
    private const val POINTS_SPLITTER = '|'
    private const val COORDINATES_SPLITTER = ','

    fun routeStringToLatLngList(str: String): List<LatLng> {
        if (str.isEmpty()) return emptyList()

        return str.split(POINTS_SPLITTER).map {
            val point = it.split(COORDINATES_SPLITTER).map { d -> d.toDoubleOrNull() }

            if (point.size != 2) return emptyList()
            val (latitude, longitude) = point

            if (latitude == null || longitude == null) return emptyList()
            LatLng(latitude, longitude)
        }
    }

    fun routeListToString(route: List<LatLng>): String = buildString {
        for (i in route.indices) {
            append("${route[i].latitude}${COORDINATES_SPLITTER}${route[i].longitude}")
            if (i != route.lastIndex) append(POINTS_SPLITTER)
        }
    }

    fun snapshotByteArrayToBitmap(byteArray: ByteArray) =
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)!!
    fun snapshotBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }

    fun centerLatLngToString(center: LatLng) =
        "${center.latitude}${COORDINATES_SPLITTER}${center.longitude}"
    fun centerStringToLatLng(str: String): LatLng {
        val (lat, lng) = str.split(COORDINATES_SPLITTER).map { it.toDouble() }
        return LatLng(lat, lng)
    }
}