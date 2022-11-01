package org.theromanticdev.unusualfitnessapp.dagger.app

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import org.theromanticdev.unusualfitnessapp.dagger.trainFragment.WorkoutFragmentComponent
import org.theromanticdev.unusualfitnessapp.presentation.view.fragments.WorkoutResultFragment
import org.theromanticdev.unusualfitnessapp.services.WorkoutService
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    fun injectIntoWorkoutService(service: WorkoutService)

    fun injectIntoWorkoutResultFragment(fragment: WorkoutResultFragment)

    fun trainComponent(): WorkoutFragmentComponent.Builder

    fun context(): Context

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

}