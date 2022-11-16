package org.theromanticdev.unusualfitnessapp.data.repositories

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.theromanticdev.unusualfitnessapp.data.sqlite.DatabaseContract.WorkoutsTable
import org.theromanticdev.unusualfitnessapp.data.sqlite.DatabaseHelper
import org.theromanticdev.unusualfitnessapp.domain.models.WorkoutInfo
import org.theromanticdev.unusualfitnessapp.domain.repository.DatabaseRepository
import java.util.*

class SQLiteRepository(private val applicationContext: Context) : DatabaseRepository {

    private val database: SQLiteDatabase by lazy {
        DatabaseHelper(applicationContext).writableDatabase
    }

    override fun getAllWorkoutInfo(): Map<Int, WorkoutInfo> {
        val cursor = database.query(
            WorkoutsTable.TABLE_NAME,
            null,
            null,
            null,
            null, null, null
        )

        return cursor.use {
            val result = mutableMapOf<Int, WorkoutInfo>()

            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_ID))
                result[id] = getWorkoutInfoFromCursor(cursor)
            }

            result
        }
    }

    override fun addWorkoutInfo(workoutInfo: WorkoutInfo) {
        val contentValues = ContentValues().apply {
            put(WorkoutsTable.COLUMN_TYPE, workoutInfo.type)
            put(WorkoutsTable.COLUMN_START_TIME, workoutInfo.startTime)
            put(WorkoutsTable.COLUMN_FINISH_TIME, workoutInfo.finishTime)
            put(WorkoutsTable.COLUMN_DISTANCE, workoutInfo.distance)
            put(WorkoutsTable.COLUMN_DURATION, workoutInfo.duration)
            put(WorkoutsTable.COLUMN_SNAPSHOT, workoutInfo.snapshot)
            put(WorkoutsTable.COLUMN_ROUTE, workoutInfo.route)
            put(WorkoutsTable.COLUMN_CENTER, workoutInfo.centerPoint)
            put(WorkoutsTable.COLUMN_ZOOM, workoutInfo.zoom)
        }

        database.insertOrThrow(
            WorkoutsTable.TABLE_NAME,
            null,
            contentValues
        )
    }

    override fun getWorkoutInfoById(id: Int): WorkoutInfo? {
        val cursor = database.query(
            WorkoutsTable.TABLE_NAME,
            null,
            "${WorkoutsTable.COLUMN_ID} = ?",
            arrayOf("$id"),
            null, null, null
        )

        return cursor.use {
            if (cursor.count == 0) return null
            cursor.moveToFirst()
            getWorkoutInfoFromCursor(cursor)
        }
    }

    override fun deleteWorkoutInfoById(id: Int) {
        database.execSQL("DELETE FROM workouts WHERE id=$id")
    }

    private fun getWorkoutInfoFromCursor(cursor: Cursor) = WorkoutInfo(
        type = cursor.getInt(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_TYPE)),
        startTime = cursor.getLong(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_START_TIME)),
        finishTime = cursor.getLong(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_FINISH_TIME)),
        distance = cursor.getInt(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_DISTANCE)),
        route = cursor.getString(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_ROUTE)),
        duration = cursor.getInt(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_DURATION)),
        snapshot = cursor.getBlob(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_SNAPSHOT)),
        zoom = cursor.getFloat(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_ZOOM)),
        centerPoint = cursor.getString(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_CENTER))
    )

}