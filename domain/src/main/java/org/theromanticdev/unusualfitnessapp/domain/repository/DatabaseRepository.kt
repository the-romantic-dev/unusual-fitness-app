package org.theromanticdev.unusualfitnessapp.domain.repository

import org.theromanticdev.unusualfitnessapp.domain.models.WorkoutInfo
import java.util.*

interface DatabaseRepository {

    fun getAllWorkoutInfo(): Map<Int, WorkoutInfo>

    fun addWorkoutInfo(workoutInfo: WorkoutInfo)

    fun getWorkoutInfoById(id: Int): WorkoutInfo?

    fun deleteWorkoutInfoById(id: Int)

}
