package ru.maxn.mescleaner.roomObjects


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Log(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var deleted: Int,
    var timestamp: String
)

@Entity
data class debugLog(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var message: String,
    var time: String
)