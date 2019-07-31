package ru.maxn.mescleaner

import android.Manifest
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.main_activity.*
import java.io.File


const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0


class MainActivity : AppCompatActivity() {


    private var dir = File("/")
    var files = mutableListOf<File>()
    var countDeleted = 0

    private val prefs: SharedPreferences by lazy {

        PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        fab.setOnClickListener {
            if (prefs.getBoolean("enable_telegram", false)) {
                clean("telegram")
            }
            if (prefs.getBoolean("enable_viber", false)) {
                clean("viber")
            }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_layout, SettingsFragment())
            .commit()
    }

    fun clean(res: String) {
        val rootPath = Environment.getExternalStorageDirectory().absolutePath
        if (res == "telegram") {
            val path = "$rootPath/Telegram"
            dir = File(path)
            addFiles(dir)
            val depth = prefs.getInt("archive_deep_telegram", 30)
            clearFiles(depth)
            files.clear()
        }
        if (res == "viber") {
            val path = "$rootPath/Viber"
            dir = File(path)
            addFiles(dir)
            val depth = prefs.getInt("archive_deep_viber", 30)
            clearFiles(depth)
            files.clear()
        }
        val text = "Deleted $countDeleted files"
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()

    }

    private fun clearFiles(depth: Int){
        for (f in files) {
            val diff = Utils.diffDays(f.lastModified())
            if (diff > depth) {
                f.delete()
                countDeleted++
            }
        }
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


//    override fun onPause() {
//        super.onPause()
//        CleanerService.scheduleService(applicationContext)
//    }
}
