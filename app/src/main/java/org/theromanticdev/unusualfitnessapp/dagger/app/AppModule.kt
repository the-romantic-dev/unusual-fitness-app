package org.theromanticdev.unusualfitnessapp.dagger.app

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.Module
import dagger.Provides
import org.theromanticdev.unusualfitnessapp.dagger.trainFragment.TrainFragmentComponent

@Module(subcomponents = [TrainFragmentComponent::class])
class AppModule {

    @Provides
    fun provideLocationRequest(): LocationRequest {
        return LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000L
        ).setMinUpdateIntervalMillis(5000).build()
    }

    @Provides
    fun provideFusedLocationProvider(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}