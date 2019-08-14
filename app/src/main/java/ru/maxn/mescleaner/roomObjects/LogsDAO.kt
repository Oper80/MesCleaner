package ru.maxn.mescleaner.roomObjects

import androidx.room.*

@Dao
interface LogsDAO {
    @Query("SELECT * FROM Log")
    fun allLogs(): List<Log>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLog(log: Log)
}

@Dao
interface DebugDao {
    @Query("SELECT * FROM debugLog")
    fun showDebugLogs(): List<debugLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(log: debugLog)
}