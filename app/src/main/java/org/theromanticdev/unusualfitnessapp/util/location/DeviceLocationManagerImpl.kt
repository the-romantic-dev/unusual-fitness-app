package org.theromanticdev.unusualfitnessapp.util.location

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DeviceLocationManagerImpl(
    private val locationProvider: FusedLocationProviderClient,
    private val settingsClient: SettingsClient,
    private val context: Context

) : DeviceLocationManager {
    private val accuracy = Priority.PRIORITY_HIGH_ACCURACY
    private val cancellationToken = CancellationTokenSource().token
    private val requestInterval = 5000L
    private val minRequestInterval = 5000L

    private val locationRequest: LocationRequest =
        LocationRequest.Builder(accuracy, requestInterval)
            .setMinUpdateIntervalMillis(minRequestInterval).setWaitForAccurateLocation(true).build()

    override suspend fun getSingleLocation(): LatLng? = suspendCoroutine { continuation ->
        locationProvider.getCurrentLocation(accuracy, cancellationToken)
            .addOnSuccessListener { current ->
                if (current != null) continuation.resume(
                    LatLng(current.latitude, current.longitude)
                ) else {
                    continuation.resume(null)
                }
            }.addOnCanceledListener {
                continuation.resume(null)
            }.addOnFailureListener {
                continuation.resume(null)
            }
    }

    override fun startLocationUpdates(listener: LocationListener) {
        locationProvider.requestLocationUpdates(
            locationRequest,
            listener,
            Looper.getMainLooper()
        )
    }

    override fun stopLocationUpdates(listener: LocationListener) {
        locationProvider.removeLocationUpdates(listener)
    }

    override suspend fun getSettingsRequestOrNull(): IntentSenderRequest? = suspendCoroutine { continuation ->
        settingsClient.checkLocationSettings(
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        ).addOnSuccessListener {
            continuation.resume(null)
        }.addOnFailureListener { exception ->
            if (exception is ApiException &&
                exception.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED
            ) {
                continuation.resume(IntentSenderRequest.Builder(exception.status.resolution!!).build())
            } else {
                continuation.resume(null)
            }
        }
    }

    override fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED





}