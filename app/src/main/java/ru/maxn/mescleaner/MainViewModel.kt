package ru.maxn.mescleaner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.PeriodicWorkRequest
import java.util.concurrent.TimeUnit


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val mWorkManager: WorkManager = WorkManager.getInstance(application)

    internal fun applyOne() {
        mWorkManager.enqueue(OneTimeWorkRequest.from(CleanerWorker::class.java))
    }

    internal fun applySchedule() {
        mWorkManager.enqueue(PeriodicWorkRequest.Builder(CleanerWorker::class.java, 15, TimeUnit.MINUTES).build())
    }

    internal fun cancelAll() {
        mWorkManager.cancelAllWorkByTag(CleanerWorker.TAG)
    }
}