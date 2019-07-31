package ru.maxn.mescleaner

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import java.io.File
import android.os.Environment
import java.util.*

const val DEPTH = 10
const val INTERVAL = 60000L

class MyBinder(val servc: CleanerService) : Binder() {
    fun getService(): CleanerService {
        return servc
    }
}

class CleanerService : Service() {
    private var dir = File("/")
    var files = mutableListOf<File>()
    private val binder: IBinder = MyBinder(this)

    override fun onBind(intent: Intent): IBinder {
        Toast.makeText(applicationContext, "I created", Toast.LENGTH_LONG).show()
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val rootPath = Environment.getExternalStorageDirectory().absolutePath
        val telegramPath = "$rootPath/Telegram"
        dir = File(telegramPath)
        addFiles(dir)
        val text = clearFiles(DEPTH).toString()
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
        scheduleService(applicationContext)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun clearFiles(depth:Int): Int {
        var count = 0
        for (f in files) {
            val diff = Utils.diffDays(f.lastModified())
            if (diff > depth) {
                f.delete()
                count++
            }
        }
        return count
    }

    private fun addFiles(dir: File) {
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

    companion object {
        fun scheduleService(context: Context) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val i = Intent(context, CleanerService::class.java)
            val pi = PendingIntent.getService(context, 0, i, 0)
            am.cancel(pi)
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL, pi)
        }
    }
}
