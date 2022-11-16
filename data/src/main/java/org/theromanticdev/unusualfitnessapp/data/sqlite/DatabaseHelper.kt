package org.theromanticdev.unusualfitnessapp.data.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import java.sql.SQLException

class DatabaseHelper(private val applicationContext: Context) :
    SQLiteOpenHelper(applicationContext, "database.db", null, 1) {

    override fun onCreate(database: SQLiteDatabase) {
        val sql = applicationContext.assets.open("db_init.sql").bufferedReader().use {
            it.readText()
        }

        sql.split(';').filter { it.isNotBlank() }.forEach {
            try {
                database.execSQL(it)
            } catch (e: SQLException) {
                println(e.errorCode)
            }
        }
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}