package ru.maxn.mescleaner

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import java.io.File
import android.net.Uri

class MyBinder (val servc:CleanerService) : Binder(){
    fun getService():CleanerService {
        return servc
    }
}

class CleanerService : Service() {

    private var dir = File("/")
    var files = listOf<File>()

    private val binder: IBinder = MyBinder(this)

    override fun onBind(intent: Intent): IBinder {
        Toast.makeText(applicationContext, "I created", Toast.LENGTH_LONG).show()
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val list = dir.list()
        val text = list[1].toString()
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
        return super.onStartCommand(intent, flags, startId)
    }

    fun addFiles(dir : File){

        if (dir.list().isEmpty()){
            return
        }
        for(s in dir.list()){

        }

    }
}
