package org.theromanticdev.unusualfitnessapp.dagger.app

import android.app.Service
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import org.theromanticdev.unusualfitnessapp.dagger.trainFragment.TrainFragmentComponent
import org.theromanticdev.unusualfitnessapp.services.TrainingService
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    fun injectIntoTrainService(service: TrainingService)

    fun trainComponent(): TrainFragmentComponent.Builder

    fun context(): Context

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

}