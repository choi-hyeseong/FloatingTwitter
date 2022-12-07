package com.comet.floatingtwitter

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.comet.floatingtwitter.callback.ActivityCallback
import com.comet.floatingtwitter.service.FloatingService
import com.comet.floatingtwitter.util.PreferenceUtil

class MainActivity : AppCompatActivity(), ActivityCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        PreferenceUtil.init(getSharedPreferences("Floating", MODE_PRIVATE))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
    }

    private fun checkPermission() {
        //항상 마시멜로 이상
        if (!Settings.canDrawOverlays(this)) {
            //권한이 없을때
            val uri = Uri.fromParts("package", packageName, null) //제발 자바 습관 버리자..
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
            val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (!Settings.canDrawOverlays(this))
                    finish()
                else
                    supportFragmentManager.beginTransaction().add(R.id.frame, MainFragment()).commit()
            }
            activityResult.launch(intent) //오버레이 그릴 수 있으면 실행 아닌경우 종료

        }
        else
            supportFragmentManager.beginTransaction().add(R.id.frame, MainFragment()).commit()
    }


    override fun switchSetting() {
        supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.frame, SettingFragment()).commit();
    }

    override fun startService() {
        stopService()
        startService(Intent(this, FloatingService::class.java))
    }

    override fun stopService() {
        stopService(Intent(this, FloatingService::class.java))
    }

}