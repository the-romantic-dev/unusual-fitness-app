package org.theromanticdev.unusualfitnessapp.data.repositories

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.theromanticdev.unusualfitnessapp.data.sqlite.DatabaseContract.PointsTable
import org.theromanticdev.unusualfitnessapp.data.sqlite.DatabaseContract.WorkoutsTable
import org.theromanticdev.unusualfitnessapp.data.sqlite.DatabaseHelper
import org.theromanticdev.unusualfitnessapp.domain.models.Point
import org.theromanticdev.unusualfitnessapp.domain.models.WorkoutInfo
import org.theromanticdev.unusualfitnessapp.domain.repository.DatabaseRepository

class SQLiteRepository(private val applicationContext: Context) : DatabaseRepository {

    private val database: SQLiteDatabase by lazy {
        DatabaseHelper(applicationContext).writableDatabase
    }

    override fun addTrainInfo(workoutInfo: WorkoutInfo) {
        val contentValues = ContentValues().apply {
            put(WorkoutsTable.COLUMN_TYPE, workoutInfo.type)
            put(WorkoutsTable.COLUMN_START_TIME, workoutInfo.startTime)
            put(WorkoutsTable.COLUMN_FINISH_TIME, workoutInfo.finishTime)
            put(WorkoutsTable.COLUMN_DISTANCE, workoutInfo.distance)
            put(WorkoutsTable.COLUMN_DURATION, workoutInfo.duration)
            put(WorkoutsTable.COLUMN_SNAPSHOT, workoutInfo.snapshot)
        }

        database.insertOrThrow(
            WorkoutsTable.TABLE_NAME,
            null,
            contentValues
        )

        val id = getLastIdFromWorkouts()
        addTrainRoute(workoutInfo.route, id)
    }

    override fun getTrainInfoById(id: Int): WorkoutInfo {
        val cursor = database.query(
            WorkoutsTable.TABLE_NAME,
            arrayOf(
                WorkoutsTable.COLUMN_TYPE,
                WorkoutsTable.COLUMN_START_TIME,
                WorkoutsTable.COLUMN_FINISH_TIME,
                WorkoutsTable.COLUMN_DISTANCE
            ),
            "${WorkoutsTable.COLUMN_ID} = ?",
            arrayOf("$id"),
            null, null, null
        )

        return cursor.use {
            if (cursor.count == 0) throw Exception("DB is empty")
            cursor.moveToFirst()
            WorkoutInfo(
                type = cursor.getInt(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_TYPE)),
                startTime = cursor.getLong(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_START_TIME)),
                finishTime = cursor.getLong(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_FINISH_TIME)),
                distance = cursor.getInt(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_DISTANCE)),
                route = getTrainRouteById(id),
                duration = cursor.getInt(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_DISTANCE)),
                snapshot = cursor.getBlob(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_SNAPSHOT))
            )
        }
    }

    override fun deleteTrainInfoById(id: Int) {
        TODO("Not yet implemented")
    }

    private fun getTrainRouteById(id: Int): List<Point> {
        val cursor = database.query(
            PointsTable.TABLE_NAME,
            arrayOf(
                PointsTable.COLUMN_LATITUDE,
                PointsTable.COLUMN_LONGITUDE
            ),
            "${PointsTable.COLUMN_TRAIN_ID} = ?",
            arrayOf("$id"),
            null, null, null
        )

        return cursor.use {
            if (cursor.count == 0) throw Exception("DB is empty")
            val result = mutableListOf<Point>()
            while (cursor.moveToNext()) {
                result.add(
                    Point(
                        latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(PointsTable.COLUMN_LATITUDE)),
                        longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(PointsTable.COLUMN_LONGITUDE))
                    )
                )
            }
            result
        }
    }

    private fun addTrainRoute(points: List<Point>, id: Int) {
        val contextValues = ContentValues().apply {
            for (p in points) {
                put(PointsTable.COLUMN_TRAIN_ID, id)
                put(PointsTable.COLUMN_LATITUDE, p.latitude)
                put(PointsTable.COLUMN_LONGITUDE, p.longitude)
            }
        }

        database.insertOrThrow(
            PointsTable.TABLE_NAME,
            null,
            contextValues
        )

    }

    private fun getLastIdFromWorkouts(): Int {
        val cursor = database.query(
            WorkoutsTable.TABLE_NAME,
            arrayOf(WorkoutsTable.COLUMN_ID),
            "${WorkoutsTable.COLUMN_ID} = (SELECT MAX(\"${WorkoutsTable.COLUMN_ID}\") FROM \"${WorkoutsTable.TABLE_NAME}\")",
            null, null, null, null

        )

        return cursor.use {
            cursor.moveToFirst()
            cursor.getInt(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_ID))
        }
    }


}