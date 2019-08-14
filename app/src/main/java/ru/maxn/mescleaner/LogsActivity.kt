package ru.maxn.mescleaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.activity_logs.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import ru.maxn.mescleaner.roomObjects.LogsDatabase

class LogsActivity : AppCompatActivity() {
    private lateinit var mDb: LogsDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)
        textView.movementMethod = ScrollingMovementMethod()
        debugTextView.movementMethod = ScrollingMovementMethod()
        mDb = LogsDatabase.getInstance(applicationContext)
        doAsync {

            val list = mDb.logsDAO().allLogs()
            val debugList = mDb.debugLogsDAO().showDebugLogs()

            uiThread {
                textView.text = ""
                for (log in list) {
                    textView.append("${log.id} : ${log.deleted} : ${log.timestamp}\n")
                }
                debugTextView.text = ""
                for (log in debugList) {
                    debugTextView.append("${log.id} : ${log.message} : ${log.time}\n")
                }
            }
        }

    }
}
