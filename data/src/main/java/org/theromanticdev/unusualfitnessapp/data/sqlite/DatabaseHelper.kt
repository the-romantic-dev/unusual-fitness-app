package org.theromanticdev.unusualfitnessapp.data.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(private val applicationContext: Context) :
    SQLiteOpenHelper(applicationContext, "database.db", null, 1) {

    override fun onCreate(database: SQLiteDatabase) {
        val sql = applicationContext.assets.open("db_init.sql").bufferedReader().use {
            it.readText()
        }
        sql.split(';').filter {it.isNotBlank()}.forEach {
            database.execSQL(it)
        }
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}