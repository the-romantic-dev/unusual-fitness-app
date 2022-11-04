package org.theromanticdev.unusualfitnessapp.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.model.LatLng
import org.theromanticdev.unusualfitnessapp.appComponent
import org.theromanticdev.unusualfitnessapp.util.IntentStrings
import org.theromanticdev.unusualfitnessapp.util.location.DeviceLocationManager
import javax.inject.Inject

class WorkoutService() : Service() {

    @Inject
    lateinit var deviceLocationManager: DeviceLocationManager

    private val locationUpdatesListener = LocationListener { sendLocation(it) }

    private val workoutPauseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopLocationUpdates()
        }
    }
    private val workoutResumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            startLocationUpdates()
        }

    }

    private lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        appComponent.injectIntoWorkoutService(this)
        localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext)
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            workoutPauseReceiver,
            IntentFilter(IntentStrings.WORKOUT_PAUSED_ACTION)
        )
        localBroadcastManager.registerReceiver(
            workoutResumeReceiver,
            IntentFilter(IntentStrings.WORKOUT_RESUMED_ACTION)
        )
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        stopLocationUpdates()
    }

    private fun sendLocation(location: Location) {
        val locationIntent = Intent(IntentStrings.USER_LOCATION_UPDATES_ACTION)
        locationIntent.apply {
            putExtra(IntentStrings.LATITUDE_EXTRA, location.latitude)
            putExtra(IntentStrings.LONGITUDE_EXTRA, location.longitude)
/*            putExtra(IntentStrings.LATITUDE_EXTRA, testList[i].latitude)
            putExtra(IntentStrings.LONGITUDE_EXTRA, testList[i].longitude)
            i++
            if (i >= testList.size) i = 0*/
        }
        localBroadcastManager.sendBroadcast(locationIntent)
    }

    var i = 0

    private val testList = mutableListOf(
        LatLng(59.948514, 30.377725),
        LatLng(59.948675, 30.369550),
        LatLng(59.947010, 30.368241),
        LatLng(59.944366, 30.368434),
        LatLng(59.943990, 30.377596)
    )


    fun startLocationUpdates() {
        deviceLocationManager.startLocationUpdates(locationUpdatesListener)
    }

    fun stopLocationUpdates() {
        deviceLocationManager.stopLocationUpdates(locationUpdatesListener)
    }


}