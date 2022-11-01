package org.theromanticdev.unusualfitnessapp.util.location

import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.model.LatLng

interface DeviceLocationManager {
    suspend fun getSingleLocation(): LatLng?
    fun startLocationUpdates(listener: LocationListener)
    fun stopLocationUpdates(listener: LocationListener)
    suspend fun getSettingsRequestOrNull(): IntentSenderRequest?
    fun isPermissionGranted(): Boolean
}