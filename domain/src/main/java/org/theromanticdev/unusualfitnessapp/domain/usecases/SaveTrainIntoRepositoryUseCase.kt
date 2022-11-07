package org.theromanticdev.unusualfitnessapp.domain.usecases

import org.theromanticdev.unusualfitnessapp.domain.models.WorkoutInfo
import org.theromanticdev.unusualfitnessapp.domain.repository.DatabaseRepository

class SaveTrainIntoRepositoryUseCase(
    private val repository: DatabaseRepository
) {

    fun execute(workoutInfo: WorkoutInfo) {
        repository.addTrainInfo(workoutInfo)
    }
}