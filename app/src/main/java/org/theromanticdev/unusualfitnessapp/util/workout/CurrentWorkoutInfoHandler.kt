package org.theromanticdev.unusualfitnessapp.util.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import org.theromanticdev.unusualfitnessapp.domain.util.RouteSizeHandler
import org.theromanticdev.unusualfitnessapp.domain.util.MapCalculator
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class CurrentWorkoutInfoHandler {
    var type: Int = 0

    private val _routePoints = mutableListOf<LatLng>()
    val route: List<LatLng> get() = _routePoints

    private var _timer = MutableLiveData(0)
    val timer: LiveData<Int> get() = _timer

    private var routeLengthDegrees = 0.0

    var startTime: Long = 0
    var endTime: Long = 0

    val routeSizeHandler = RouteSizeHandler()

    private var latitudesSum = 0.0
    private var longitudesSum = 0.0

    val routeLengthMeters: Int
        get() = MapCalculator.degreesToMeters(routeLengthDegrees)

    val geometricCenterPoint: LatLng
        get() = LatLng(routeSizeHandler.centerVertical, routeSizeHandler.centerHorizontal)

    val routeWidthMeters
        get() = MapCalculator.degreesToMeters(routeSizeHandler.routeWidthDegrees)

    val routeHeightMeters
        get() = MapCalculator.degreesToMeters(routeSizeHandler.routeHeightDegrees)



    fun addPointToRoute(point: LatLng) {
        latitudesSum += point.latitude
        longitudesSum += point.longitude
        if (route.isNotEmpty()) {
            val distance = route.last() distanceTo point
            routeLengthDegrees += distance
        }
        _routePoints.add(point)
        routeSizeHandler.nextPoint(
            point.latitude,
            point.longitude
        )
    }

    fun increaseTimerByOneSecond() {
        _timer.value = _timer.value!! + 1
    }

    private infix fun LatLng.distanceTo(point2: LatLng): Double {
        val width = abs(this.longitude - point2.longitude)
        val height = abs(this.latitude - point2.latitude)
        return sqrt(width.pow(2) + height.pow(2))
    }
}