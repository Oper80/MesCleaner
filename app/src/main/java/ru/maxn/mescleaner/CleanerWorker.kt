package ru.maxn.mescleaner

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.maxn.mescleaner.roomObjects.Log
import ru.maxn.mescleaner.roomObjects.LogsDatabase
import java.io.File
import java.sql.Timestamp
import java.util.*
import org.jetbrains.anko.doAsync
import ru.maxn.mescleaner.roomObjects.debugLog

class CleanerWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {
        const val TAG = "CleanupWorker"
    }

    private var dir = File("/")
    private lateinit var mDb: LogsDatabase
    var files = mutableListOf<File>()
    var countDeleted: Int = 0

    private val prefs: SharedPreferences by lazy {

        PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    override fun doWork(): Result {
        mDb = LogsDatabase.getInstance(applicationContext)

        //Debug info
        val newDebugLog = debugLog(message = "doWork", time = Timestamp(Date().time).toString())
        doAsync {
            mDb.debugLogsDAO().insert(newDebugLog)
        }
        //Debug info


        if (prefs.getBoolean("enable_telegram", false)) {
            clean("telegram")
        }
        if (prefs.getBoolean("enable_viber", false)) {
            clean("viber")
        }

        return Result.success()
    }

    private fun clean(res: String) {
        //Debug info
        val debugLog = debugLog(message = "clean", time = Timestamp(Date().time).toString())
        doAsync {
            mDb.debugLogsDAO().insert(debugLog)
        }
        //Debug info
        val rootPath = Environment.getExternalStorageDirectory().absolutePath
        if (res == "telegram") {
            if (prefs.getBoolean("enable_telegram_images", false)) {
                val path = "$rootPath/Telegram/Telegram Images"
                dir = File(path)
                addFiles(dir)
            }
            if (prefs.getBoolean("enable_telegram_video", false)) {
                val path = "$rootPath/Telegram/Telegram Video"
                dir = File(path)
                addFiles(dir)
            }
            if (prefs.getBoolean("enable_telegram_audio", false)) {
                val path = "$rootPath/Telegram/Telegram Audio"
                dir = File(path)
                addFiles(dir)
            }
            if (prefs.getBoolean("enable_telegram_doc", false)) {
                val path = "$rootPath/Telegram/Telegram Documents"
                dir = File(path)
                addFiles(dir)
            }

            val depth = prefs.getInt("archive_deep_telegram", 30)
            deleteFiles(depth)
        }
        if (res == "viber") {
            val path = "$rootPath/Viber"
            dir = File(path)
            addFiles(dir)
            val depth = prefs.getInt("archive_deep_viber", 30)
            deleteFiles(depth)
        }
        val newLog = Log(deleted = countDeleted, timestamp = Timestamp(Date().time).toString())
        doAsync {
            mDb.logsDAO().insertLog(newLog)
        }
        countDeleted = 0
    }

    private fun deleteFiles(depth: Int) {
        clearFiles(depth)
        files.clear()
    }

    private fun clearFiles(depth: Int) {
        for (f in files) {
            val diff = Utils.diffDays(f.lastModified())
            if (diff > depth) {
                f.delete()
                countDeleted++
            }
        }
    }

    private fun addFiles(dir: File) {

        //Debug info
        val debugLog = debugLog(message = "addFiles", time = Timestamp(Date().time).toString())
        doAsync {
            mDb.debugLogsDAO().insert(debugLog)
        }
        //Debug info

        if (dir.listFiles() != null) {
            for (s in dir.listFiles()!!) {
                if (s.isDirectory) {
                    addFiles(s)
                } else {
                    files.add(s)
                }
            }
        }
    }
}
