package org.theromanticdev.unusualfitnessapp.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.*
import org.theromanticdev.unusualfitnessapp.appComponent
import org.theromanticdev.unusualfitnessapp.dagger.app.AppComponent
import java.lang.Thread.sleep
import javax.inject.Inject

class TrainingService() : Service() {
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var locationRequest: LocationRequest

    private val locationUpdatesListener = LocationListener { sendLocation(it) }

    private lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        Log.e("MapRoutingControlling", "Сервис создан")
        appComponent.injectIntoTrainService(this)
        localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext)
    }


    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationUpdatesListener,
            Looper.getMainLooper()
        )
        return START_STICKY
    }

    override fun onDestroy() {
        Log.e("MapRoutingControlling", "Сервис уничтожен")
        fusedLocationProviderClient.removeLocationUpdates(locationUpdatesListener)
    }

    private fun sendLocation(location: Location) {
        val locationIntent = Intent("UserLocationUpdates")
        locationIntent.apply {
            putExtra("latitude", location.latitude)
            putExtra("longitude", location.longitude)
        }
        localBroadcastManager.sendBroadcast(locationIntent)
    }


}