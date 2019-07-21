package ru.maxn.mescleaner

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import java.io.File
import android.net.Uri
import android.os.Environment
import java.util.*

const val SECONDS = 1000L
const val MINUTES = 60 * SECONDS
const val HOURS = 60 * MINUTES
const val DAYS = 24 * HOURS

const val ARCHIVE_DEEP = 5 * DAYS

class MyBinder (val servc:CleanerService) : Binder(){
    fun getService():CleanerService {
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
        var rootPath = Environment.getExternalStorageDirectory().absolutePath
        val telegramPath = rootPath + "/Telegram"
        dir = File(telegramPath)
        val list = dir.list()
        val text = list[0].toString()
        addFiles(dir)
        clearFiles()
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun clearFiles() {
        for(f in files){
            val curDate = Date().time
            val fileDate = f.lastModified()
            if(Date().time - f.lastModified() > ARCHIVE_DEEP){
                f.delete()
            }
        }
    }

    fun addFiles(dir : File){

        if (dir.list().isEmpty()){
            return
        }
        for(s in dir.listFiles()){
            if(s.isDirectory){
                addFiles(s)
            }else{
                files.add(s)
            }
        }

    }
}
