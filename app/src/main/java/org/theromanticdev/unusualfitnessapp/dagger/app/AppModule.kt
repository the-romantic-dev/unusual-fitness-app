package org.theromanticdev.unusualfitnessapp.dagger.app

import android.content.Context
import android.content.res.Resources
import com.google.android.gms.location.*
import dagger.Module
import dagger.Provides
import org.theromanticdev.unusualfitnessapp.dagger.trainFragment.WorkoutFragmentComponent
import org.theromanticdev.unusualfitnessapp.util.location.DeviceLocationManager
import org.theromanticdev.unusualfitnessapp.util.location.DeviceLocationManagerImpl
import javax.inject.Singleton

@Module(subcomponents = [WorkoutFragmentComponent::class])
class AppModule {

    @Provides
    fun provideSettingsClient(context: Context): SettingsClient {
        return LocationServices.getSettingsClient(context)
    }

    @Provides
    fun provideResources(context: Context): Resources = context.resources

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