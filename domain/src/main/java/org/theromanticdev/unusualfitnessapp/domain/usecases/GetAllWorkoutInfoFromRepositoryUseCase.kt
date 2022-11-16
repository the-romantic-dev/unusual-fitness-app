package org.theromanticdev.unusualfitnessapp.domain.usecases

import org.theromanticdev.unusualfitnessapp.domain.models.WorkoutInfo
import org.theromanticdev.unusualfitnessapp.domain.repository.DatabaseRepository

class GetAllWorkoutInfoFromRepositoryUseCase(
    private val repository: DatabaseRepository
) {
    fun execute(): Map<Int, WorkoutInfo> {
        return repository.getAllWorkoutInfo()
    }
}