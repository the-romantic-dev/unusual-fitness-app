package org.theromanticdev.unusualfitnessapp.domain.usecases

import org.theromanticdev.unusualfitnessapp.domain.models.TrainInfo
import org.theromanticdev.unusualfitnessapp.domain.repository.DatabaseRepository

class GetTrainFromRepositoryUseCase(
    private val repository: DatabaseRepository
) {
    fun execute(id: Int) = repository.getTrainInfoById(id)
}