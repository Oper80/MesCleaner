package ru.maxn.mescleaner

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast

class MyBinder (val servc:CleanerService) : Binder(){
    fun getService():CleanerService {
        return servc
    }
}

class CleanerService : Service() {

    private val binder: IBinder = MyBinder(this)

    override fun onBind(intent: Intent): IBinder {
        Toast.makeText(applicationContext, "I created", Toast.LENGTH_LONG).show()
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(applicationContext, "I started", Toast.LENGTH_LONG).show()
        return super.onStartCommand(intent, flags, startId)
    }
}
