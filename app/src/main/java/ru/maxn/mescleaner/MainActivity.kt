package ru.maxn.mescleaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    override fun onPause() {
        super.onPause()


        val context = applicationContext
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, CleanerService::class.java)
        val pi = PendingIntent.getService(context, 0, i, 0)
        am.cancel(pi)
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pi)
    }
}
