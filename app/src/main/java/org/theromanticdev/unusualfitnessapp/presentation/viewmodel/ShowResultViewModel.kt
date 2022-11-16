package org.theromanticdev.unusualfitnessapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import org.theromanticdev.unusualfitnessapp.domain.models.WorkoutInfo

class ShowResultViewModel: ViewModel() {
    var workoutInfo: WorkoutInfo? = null
}