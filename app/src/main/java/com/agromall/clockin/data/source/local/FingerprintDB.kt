package com.agromall.clockin.data.source.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FingerprintDB(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSIOM) {

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME " +
                "($COL_IDKIT_ENGINE_ID TEXT PRIMARY KEY, " +
                "$COL_IDKIT_ENGINE_CUSTOM TEXT, " +
                "$COL_IDKIT_ENGINE_RECORD TEXT, " +
                "$COL_IDKIT_ENGINE_IMAGES TEXT, " +
                "$COL_IDKIT_ENGINE_IMAGES_ID TEXT" +
                "$COL_IDKIT_ENGINE_IMAGES_TEMPLATE TEXT, " +
                "$COL_IDKIT_ENGINE_TITLE TEXT, " +
                "$COL_IDKIT_ENGINE_VALUE TEXT, " +
                "$COL_IDKIT_ENGINE_TAG_ID TEXT" +
                "$COL_IDKIT_ENGINE_TAG_VALUE TEXT, " +
                "$COL_IDKIT_ENGINE_TAG_NAME TEXT" +
                ")"
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Called when the database needs to be upgraded
    }


    companion object {
        val DB_NAME = "FDB"
        private val DB_VERSIOM = 1
        private val TABLE_NAME = "users"
        /**
         * column names for Innovatrics db table
         */

        val COL_IDKIT_ENGINE_ID = "userid"
        val COL_IDKIT_ENGINE_RECORD = "record"
        val COL_IDKIT_ENGINE_CUSTOM = "custom_data"

        val COL_IDKIT_ENGINE_IMAGES_ID = "useridx"
        val COL_IDKIT_ENGINE_IMAGES_TEMPLATE = "templateid"
        val COL_IDKIT_ENGINE_IMAGES = "image"

        val COL_IDKIT_ENGINE_TITLE = "title"
        val COL_IDKIT_ENGINE_VALUE = "value"

        val COL_IDKIT_ENGINE_TAG_ID = "userids"
        val COL_IDKIT_ENGINE_TAG_NAME = "name"
        val COL_IDKIT_ENGINE_TAG_VALUE = "values"
    }
}