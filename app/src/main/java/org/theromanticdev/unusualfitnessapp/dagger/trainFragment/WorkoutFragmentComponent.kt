package org.theromanticdev.unusualfitnessapp.dagger.trainFragment

import dagger.BindsInstance
import dagger.Subcomponent
import org.theromanticdev.unusualfitnessapp.presentation.view.fragments.WorkoutFragment
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.WorkoutViewModel

@Subcomponent(modules = [WorkoutModule::class])
interface WorkoutFragmentComponent {

    fun injectIntoViewModel(viewModel: WorkoutViewModel)

    fun injectIntoFragment(fragment: WorkoutFragment)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun trainFragment(fragment: WorkoutFragment): Builder

        fun build(): WorkoutFragmentComponent
    }
}