package org.theromanticdev.unusualfitnessapp.data.sqlite

object DatabaseContract {
    object WorkoutsTable {
        const val TABLE_NAME = "workouts"
        const val COLUMN_ID = "id"
        const val COLUMN_TYPE = "type"
        const val COLUMN_START_TIME = "start_time"
        const val COLUMN_FINISH_TIME = "finish_time"
        const val COLUMN_DISTANCE = "distance"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_SNAPSHOT = "snapshot"
        const val COLUMN_ROUTE = "route"
        const val COLUMN_ZOOM = "zoom"
        const val COLUMN_CENTER = "center"
    }
}