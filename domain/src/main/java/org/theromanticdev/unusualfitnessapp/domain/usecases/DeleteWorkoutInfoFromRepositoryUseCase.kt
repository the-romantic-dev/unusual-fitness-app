package org.theromanticdev.unusualfitnessapp.domain.usecases

import org.theromanticdev.unusualfitnessapp.domain.repository.DatabaseRepository

class DeleteWorkoutInfoFromRepositoryUseCase(
    private val repository: DatabaseRepository
) {
    fun execute(id: Int) {
        repository.deleteWorkoutInfoById(id)
    }
}