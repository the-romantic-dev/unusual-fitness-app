package org.theromanticdev.unusualfitnessapp.dagger.app

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import dagger.Module
import dagger.Provides
import org.theromanticdev.unusualfitnessapp.util.location.DeviceLocationManager
import org.theromanticdev.unusualfitnessapp.util.location.DeviceLocationManagerImpl
import javax.inject.Singleton

@Module
class LocationModule {

    @Provides
    fun provideSettingsClient(context: Context): SettingsClient {
        return LocationServices.getSettingsClient(context)
    }

    @Provides
    fun provideFusedLocationProvider(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideDeviceLocationManager(
        locationProvider: FusedLocationProviderClient,
        settingsClient: SettingsClient,
        context: Context
    ): DeviceLocationManager {
        return DeviceLocationManagerImpl(locationProvider, settingsClient, context)
    }
}