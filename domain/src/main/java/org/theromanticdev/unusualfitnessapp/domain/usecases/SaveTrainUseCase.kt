package org.theromanticdev.unusualfitnessapp.domain.usecases

import org.theromanticdev.unusualfitnessapp.domain.models.TrainInfo
import org.theromanticdev.unusualfitnessapp.domain.repository.DatabaseRepository

class SaveTrainUseCase(
    private val repository: DatabaseRepository
) {

    fun execute(trainInfo: TrainInfo) {
        repository.addTrainInfo(trainInfo)
    }
}