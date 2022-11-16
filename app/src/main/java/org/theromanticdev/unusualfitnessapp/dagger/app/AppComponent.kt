package org.theromanticdev.unusualfitnessapp.dagger.app

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import org.theromanticdev.unusualfitnessapp.presentation.view.fragments.WorkoutFragment
import org.theromanticdev.unusualfitnessapp.presentation.view.fragments.WorkoutResultFragment
import org.theromanticdev.unusualfitnessapp.presentation.view.fragments.WorkoutsListFragment
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.WorkoutViewModel
import org.theromanticdev.unusualfitnessapp.services.WorkoutService
import javax.inject.Singleton

@Component(modules = [AppModule::class, LocationModule::class])
@Singleton
interface AppComponent {

    fun inject(fragment: WorkoutFragment)

    fun inject(viewModel: WorkoutViewModel)

    fun inject(service: WorkoutService)

    fun inject(fragment: WorkoutResultFragment)

    fun inject(fragment: WorkoutsListFragment)

    fun context(): Context

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

}