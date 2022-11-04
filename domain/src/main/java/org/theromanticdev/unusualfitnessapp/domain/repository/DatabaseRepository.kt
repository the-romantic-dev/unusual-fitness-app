package org.theromanticdev.unusualfitnessapp.domain.repository

import org.theromanticdev.unusualfitnessapp.domain.models.Point
import org.theromanticdev.unusualfitnessapp.domain.models.TrainInfo

interface DatabaseRepository {

    fun addTrainInfo(trainInfo: TrainInfo)

    fun getTrainInfoById(id: Int): TrainInfo

    fun deleteTrainInfoById(id: Int)

}
