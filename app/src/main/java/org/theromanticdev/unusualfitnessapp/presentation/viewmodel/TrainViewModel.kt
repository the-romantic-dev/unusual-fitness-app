package org.theromanticdev.unusualfitnessapp.presentation.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TrainViewModel : ViewModel() {

    var lastLocation: LatLng? = null

    val trainButtonState = MutableLiveData(true)
    val isLocationProvided = MutableLiveData(false)
    val timer = MutableLiveData(0)

    private lateinit var timerStarter: Job


    @Inject
    lateinit var locationProvider: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    suspend fun getInitialLocation(): Location? = suspendCoroutine { continuation ->
        locationProvider.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener {current ->
            if (current != null) {
                lastLocation = LatLng(current.latitude, current.longitude)
                if (isLocationProvided.value == false) isLocationProvided.value = true
                continuation.resume(current)
            }
            else {
                locationProvider.lastLocation.addOnSuccessListener {last ->
                    if (last != null) continuation.resume(last)
                    else continuation.resume(null)
                }
            }
        }
    }

    fun startTimer() {
        timerStarter = viewModelScope.launch {
            while(isActive) {
                delay(1000)
                timer.value = timer.value!! + 1
            }
        }

        Log.e("the_romantic_dev", "${timerStarter.isActive}")
    }

    fun stopTimer() {
        timerStarter.cancel()
    }

}