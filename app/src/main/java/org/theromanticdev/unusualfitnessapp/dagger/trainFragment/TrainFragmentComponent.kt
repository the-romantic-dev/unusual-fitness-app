package org.theromanticdev.unusualfitnessapp.dagger.trainFragment

import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import org.theromanticdev.unusualfitnessapp.dagger.app.AppComponent
import org.theromanticdev.unusualfitnessapp.presentation.view.fragments.TrainFragment
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.TrainViewModel

@Subcomponent(modules = [TrainModule::class])
interface TrainFragmentComponent {

    fun injectIntoViewModel(viewModel: TrainViewModel)

    fun injectIntoFragment(fragment: TrainFragment)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun trainFragment(fragment: TrainFragment): Builder

/*        @BindsInstance
        fun trainViewModel(viewModel: TrainViewModel): Builder*/

        fun build(): TrainFragmentComponent
    }
}