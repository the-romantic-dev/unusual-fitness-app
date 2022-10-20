package org.theromanticdev.unusualfitnessapp.util

import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import javax.inject.Inject

class LocationSettingsChecker @Inject constructor(
    private val settingsClient: SettingsClient,
    private val locationSettingsRequestBuilder: LocationSettingsRequest.Builder
) {
    fun check(): Task<LocationSettingsResponse> {
        return settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build())
    }
}