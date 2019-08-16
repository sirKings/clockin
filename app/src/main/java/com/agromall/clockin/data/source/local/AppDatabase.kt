package com.agromall.clockin.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.agromall.clockin.data.dto.Attendance
import com.agromall.clockin.data.dto.FingerprintsModel
import com.agromall.clockin.data.dto.Staff
import com.agromall.clockin.data.source.local.AppDatabase.Companion.DB_VERSION

@Database(entities = [Staff::class, Attendance::class, FingerprintsModel::class], version = DB_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): StaffDao

    companion object {
        const val DB_VERSION = 1
        private const val DB_NAME = "clockin"
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: build(context).also { INSTANCE = it }
            }

        private fun build(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()

        private val MIGRATION_1_TO_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
    }
}