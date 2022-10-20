package org.theromanticdev.unusualfitnessapp.dagger.trainFragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.*
import dagger.Module
import dagger.Provides
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.presentation.view.fragments.TrainFragment
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.TrainViewModel
import javax.inject.Named

@Module
class TrainModule {

    @Provides
    fun provideUserMarkerBitmap(fragment: TrainFragment): Bitmap {
        return Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                fragment.requireActivity().resources,
                R.drawable.navigation_arrow
            ), 96, 96, false
        )
    }

    @Provides
    fun provideSettingsClient(context: Context): SettingsClient {
        return LocationServices.getSettingsClient(context)
    }

    @Provides
    fun provideLocationSettingsRequestBuilder(locationRequest: LocationRequest): LocationSettingsRequest.Builder {
        return LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    }
}