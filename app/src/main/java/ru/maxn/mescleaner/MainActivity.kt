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
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import kotlinx.android.synthetic.main.main_activity.*



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


    private lateinit var mViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)


        checkPermissions()

        fab.setOnClickListener {
            mViewModel.cancelAll()
            mViewModel.applyOne()
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_layout, SettingsFragment())
                .commit()
        }

        schedule_btn.setOnClickListener {
            mViewModel.cancelAll()
            mViewModel.applySchedule()
        }

        logs_btn.setOnClickListener {
            val intent = Intent(this, LogsActivity::class.java)
            startActivity(intent)
        }

        cancel_btn.setOnClickListener {
            mViewModel.cancelAll()
        }
    }

    private fun checkPermissions() {
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
    }

}
