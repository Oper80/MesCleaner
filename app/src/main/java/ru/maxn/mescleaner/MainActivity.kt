package ru.maxn.mescleaner

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import kotlinx.android.synthetic.main.main_activity.*
import java.io.File


const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0


class MainActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartScreenCallback {
    override fun onPreferenceStartScreen(caller: PreferenceFragmentCompat?, pref: PreferenceScreen?): Boolean {
        val ft = supportFragmentManager.beginTransaction()
        val fragment = SettingsFragment()
        val args = Bundle()
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref?.key)
        fragment.arguments = args
        ft.replace(R.id.settings_layout, fragment, pref?.key)
        ft.addToBackStack(pref?.key)
        ft.commit()
        return true
    }


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
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
            } else {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )

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
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_layout, SettingsFragment())
                .commit()
        }

        schedule_btn.setOnClickListener {
            startService(Intent(App.applicationContext(), CleanerService::class.java))
        }
    }

    private fun clean(res: String) {
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
