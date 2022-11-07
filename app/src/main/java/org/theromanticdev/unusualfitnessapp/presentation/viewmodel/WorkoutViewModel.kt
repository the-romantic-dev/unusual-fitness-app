package org.theromanticdev.unusualfitnessapp.presentation.viewmodel

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.lifecycle.*
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.*
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.util.workout.WorkoutResult
import org.theromanticdev.unusualfitnessapp.util.workout.WorkoutStates
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WorkoutViewModel : ViewModel() {

    val workoutState = MutableLiveData(WorkoutStates.STOPPED)
    var workoutResult = WorkoutResult()
    var isTimerRunning = false

    private lateinit var timerStarter: Job

    @Inject
    lateinit var resources: Resources

    val userLocationBitmap: Bitmap by lazy {
        Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources,
                R.drawable.map_user_point_circle
            ),
            resources.getInteger(R.integer.user_marker_width),
            resources.getInteger(R.integer.user_marker_height)
            , false
        )
    }

/*    @Inject
    lateinit var locationProvider: FusedLocationProviderClient*/

    fun startTimer() {
        isTimerRunning = true
        timerStarter = viewModelScope.launch {
            while(isActive) {
                delay(1000)
                workoutResult.increaseTimerByOneSecond()
            }
        }
    }

    fun stopTimer() {
        if (isTimerRunning) timerStarter.cancel()
        isTimerRunning = false
    }

}