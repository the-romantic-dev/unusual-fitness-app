package org.theromanticdev.unusualfitnessapp.presentation.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.*
import org.theromanticdev.unusualfitnessapp.util.workout.CurrentWorkoutInfoHandler
import org.theromanticdev.unusualfitnessapp.util.singletones.WorkoutStates

class WorkoutViewModel : ViewModel() {

    val workoutState = MutableLiveData(WorkoutStates.STOPPED)
    var currentWorkoutInfoHandler = CurrentWorkoutInfoHandler()
    var isTimerRunning = false

    private lateinit var timerStarter: Job

    fun startTimer() {
        isTimerRunning = true
        timerStarter = viewModelScope.launch {
            while(isActive) {
                delay(1000)
                currentWorkoutInfoHandler.increaseTimerByOneSecond()
            }
        }
    }

    fun stopTimer() {
        if (isTimerRunning) timerStarter.cancel()
        isTimerRunning = false
    }




}