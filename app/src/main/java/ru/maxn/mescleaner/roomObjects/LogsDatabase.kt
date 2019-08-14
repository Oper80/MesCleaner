package ru.maxn.mescleaner.roomObjects

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = arrayOf(Log::class, debugLog::class), version = 2)

abstract class LogsDatabase : RoomDatabase() {
    abstract fun logsDAO(): LogsDAO
    abstract fun debugLogsDAO(): DebugDao

    companion object {
        private var INSTANCE: LogsDatabase? = null
        @JvmField
        val MIGRATION_1_2 = Migration1To2()

        fun getInstance(context: Context): LogsDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    LogsDatabase::class.java,
                    "roomdb"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
            }
            return INSTANCE as LogsDatabase
        }
    }

    class Migration1To2 : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `debugLog` " +
                        "(`id` int AUTO_INCREMENT NOT NULL, " +
                        "`message` TEXT NOT NULL, " +
                        "`time` TEXT NOT NULL, " +
                        "PRIMARY KEY(`id`));"
            )
        }
    }
}

