package org.theromanticdev.unusualfitnessapp.data.sqlite

object DatabaseContract {
    object PointsTable {
        const val TABLE_NAME = "points"
        const val COLUMN_TRAIN_ID = "train_id"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
    }

    object WorkoutsTable {
        const val TABLE_NAME = "workouts"
        const val COLUMN_ID = "id"
        const val COLUMN_TYPE = "type"
        const val COLUMN_START_TIME = "start_time"
        const val COLUMN_FINISH_TIME = "finish_time"
        const val COLUMN_DISTANCE = "distance"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_SNAPSHOT = "snapshot"
    }
}