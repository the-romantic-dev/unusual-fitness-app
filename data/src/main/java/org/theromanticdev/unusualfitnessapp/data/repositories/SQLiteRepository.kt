package org.theromanticdev.unusualfitnessapp.data.repositories

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.theromanticdev.unusualfitnessapp.data.sqlite.DatabaseContract.PointsTable
import org.theromanticdev.unusualfitnessapp.data.sqlite.DatabaseContract.WorkoutsTable
import org.theromanticdev.unusualfitnessapp.data.sqlite.DatabaseHelper
import org.theromanticdev.unusualfitnessapp.domain.models.Point
import org.theromanticdev.unusualfitnessapp.domain.models.TrainInfo
import org.theromanticdev.unusualfitnessapp.domain.repository.DatabaseRepository

class SQLiteRepository(private val applicationContext: Context) : DatabaseRepository {

    private val database: SQLiteDatabase by lazy {
        DatabaseHelper(applicationContext).writableDatabase
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

    override fun addTrainInfo(trainInfo: TrainInfo) {
        val contextValues = ContentValues().apply {
            put(WorkoutsTable.COLUMN_TYPE, trainInfo.type)
            put(WorkoutsTable.COLUMN_START_TIME, trainInfo.startTime)
            put(WorkoutsTable.COLUMN_FINISH_TIME, trainInfo.finishTime)
            put(WorkoutsTable.COLUMN_DISTANCE, trainInfo.distance)
        }

        database.insertOrThrow(
            WorkoutsTable.TABLE_NAME,
            null,
            contextValues
        )

        val id = getLastIdFromWorkouts()
        addTrainRoute(trainInfo.route, id)
    }

    override fun getTrainInfoById(id: Int): TrainInfo {
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
            TrainInfo(
                type = cursor.getInt(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_TYPE)),
                startTime = cursor.getLong(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_START_TIME)),
                finishTime = cursor.getLong(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_FINISH_TIME)),
                distance = cursor.getInt(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_DISTANCE)),
                route = getTrainRouteById(id),
                duration = cursor.getInt(cursor.getColumnIndexOrThrow(WorkoutsTable.COLUMN_DISTANCE))
            )
        }
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

    override fun deleteTrainAndRouteById(id: Int) {
        TODO("Not yet implemented")
    }


}