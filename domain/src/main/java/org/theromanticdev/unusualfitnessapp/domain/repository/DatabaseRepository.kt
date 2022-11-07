package org.theromanticdev.unusualfitnessapp.domain.repository

import org.theromanticdev.unusualfitnessapp.domain.models.WorkoutInfo

interface DatabaseRepository {

    fun addTrainInfo(workoutInfo: WorkoutInfo)

    fun getTrainInfoById(id: Int): WorkoutInfo

    fun deleteTrainInfoById(id: Int)

}
