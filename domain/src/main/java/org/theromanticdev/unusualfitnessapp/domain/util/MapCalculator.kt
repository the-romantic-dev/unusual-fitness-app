package org.theromanticdev.unusualfitnessapp.domain.util

import kotlin.math.log
import kotlin.math.max
import kotlin.math.min

object MapCalculator {

    private const val METERS_IN_DEGREE = 111139
    private const val EARTH_EQUATOR = 40000000
    private const val MERCATOR_TALE_SIZE_IN_PIXELS = 256

    fun degreesToMeters(degrees: Double): Int = (degrees * METERS_IN_DEGREE).toInt()

    fun minZoomForHeightAndWidth(
        metersHeight: Int,
        metersWidth: Int,
        pixelsHeight: Int,
        pixelsWidth: Int
    ): Float {
        val meters = max(metersHeight, metersWidth)
        val pixels = min(pixelsHeight, pixelsWidth)
        return metersAndPixelsToZoom(meters, pixels)
    }

    fun minZoomForSquareMap(metersHeight: Int, metersWidth: Int, sideInPixels: Int) =
        minZoomForHeightAndWidth(metersHeight, metersWidth, sideInPixels, sideInPixels)

    private fun metersAndPixelsToZoom(meters: Int, pixels: Int): Float {
        val metersInPixel = meters.toDouble() / pixels
        val mercatorTalesOnScreen = EARTH_EQUATOR.toDouble() /
                (metersInPixel * MERCATOR_TALE_SIZE_IN_PIXELS)
        return log(mercatorTalesOnScreen, 2.0).toFloat()
    }

}