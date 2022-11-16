package org.theromanticdev.unusualfitnessapp.util.workout

import org.theromanticdev.unusualfitnessapp.R

class WorkoutTypes {

    val types = listOf(
        Type("Walking", R.drawable.type_icon_walking),
        Type("Running", R.drawable.type_icon_running),
        Type("Biking", R.drawable.type_icon_biking)
    )


    companion object {
        private var privateInstance: WorkoutTypes? = null
        val instance: WorkoutTypes
        get() {
            if (privateInstance == null) {
                privateInstance = WorkoutTypes()
            }
            return privateInstance!!
        }
    }

    inner class Type(
        val name: String,
        val imageResource: Int
    )


}