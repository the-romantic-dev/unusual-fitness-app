package org.theromanticdev.unusualfitnessapp.domain.util

class RouteSize(
    private var north: Double? = null,
    private var south: Double? = null,
    private var east: Double? = null,
    private var west: Double? = null
) {

    val routeWidthDegrees: Double get() = run {
        if (west == null || east == null) 0.0
        else if (east!! >= west!!) east!! - west!!
        else 360.0 + east!! - west!!
    }

    val routeHeightDegrees: Double get() = run {
        if (north == null || south == null) 0.0
        else north!! - south!!
    }

    val centerHorizontal: Double get() = run {
        if (west == null) 0.0
        else west!! + routeWidthDegrees / 2
    }

    val centerVertical: Double get() = run {
        if (south == null) 0.0
        else south!! + routeHeightDegrees / 2
    }

    fun nextPoint(latitude: Double, longitude: Double) {
        checkForNorth(latitude)
        checkForSouth(latitude)
        checkForEast(longitude)
        checkForWest(longitude)
    }


    private fun checkForNorth(latitude: Double) {
        if (north == null || latitude > north!!) {
            north = latitude
        }
    }

    private fun checkForSouth(latitude: Double) {
        if (south == null || latitude < south!!) {
            south = latitude
        }
    }

    private fun checkForEast(longitude: Double) {
        if (east == null) east = longitude
        else if (east!! > 0 && longitude < 0 || longitude > east!!) east = longitude
    }

    private fun checkForWest(longitude: Double) {
        if (west == null) west = longitude
        else if (west!! < 0 && longitude > 0 || longitude < west!!) west = longitude
    }

}
