package org.theromanticdev.unusualfitnessapp.util.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import org.theromanticdev.unusualfitnessapp.domain.util.RouteSizeHandler
import org.theromanticdev.unusualfitnessapp.domain.util.MapCalculator
import org.theromanticdev.unusualfitnessapp.util.WorkoutInfoFormatter
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

class WorkoutResult {
    private val _routePoints = mutableListOf<LatLng>()
    val route: List<LatLng> get() = _routePoints

    private var _timer = MutableLiveData(0)
    val timer: LiveData<Int> get() = _timer
    val formattedTime: String get() = WorkoutInfoFormatter.formatDuration(timer.value!!)

    private var routeLengthDegrees = 0.0
    val routeLengthMeters: Int
        get() = MapCalculator.degreesToMeters(routeLengthDegrees)
/*    val routeLengthKilometers: Double
        get() = round(routeLengthMeters / 100.0) / 10.0*/

    val geometricCenterPoint: LatLng
        get() = LatLng(routeSizeHandler.centerVertical, routeSizeHandler.centerHorizontal)

    val routeWidthMeters
        get() = MapCalculator.degreesToMeters(routeSizeHandler.routeWidthDegrees)

    val routeHeightMeters
        get() = MapCalculator.degreesToMeters(routeSizeHandler.routeHeightDegrees)

    var startTime: Long? = null
    var endTime: Long? = null


    private val routeSizeHandler = RouteSizeHandler()

    private var latitudesSum = 0.0
    private var longitudesSum = 0.0

    fun addPointToRoute(point: LatLng) {
        _routePoints.add(point)
        latitudesSum += point.latitude
        longitudesSum += point.longitude
        if (route.size > 1) routeLengthDegrees += distanceBetweenPoints(point, _routePoints.last())
        routeSizeHandler.nextPoint(
            point.latitude,
            point.longitude
        )
    }

    fun increaseTimerByOneSecond() {
        _timer.value = _timer.value!! + 1
    }

    private fun distanceBetweenPoints(p1: LatLng, p2: LatLng): Double {
        val width = abs(p1.longitude - p2.longitude)
        val height = abs(p1.latitude - p2.latitude)
        return sqrt(width.pow(2) + height.pow(2))
    }

/*    fun getInfo(): WorkoutInfo = WorkoutInfo(

    )*/
}