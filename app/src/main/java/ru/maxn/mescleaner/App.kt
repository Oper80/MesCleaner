package ru.maxn.mescleaner

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration

class App : Application(), Configuration.Provider {

    companion object {
        private var instance: App? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.VERBOSE)
            .build()
}