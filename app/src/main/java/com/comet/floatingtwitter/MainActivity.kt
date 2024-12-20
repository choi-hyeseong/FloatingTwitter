package com.comet.floatingtwitter

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.comet.floatingtwitter.callback.ActivityCallback
import com.comet.floatingtwitter.common.storage.PreferenceDataStore
import com.comet.floatingtwitter.overlay.service.FloatingService
import com.comet.floatingtwitter.twitter.setting.model.SettingData
import com.comet.floatingtwitter.twitter.setting.repository.PreferenceSettingRepository
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), ActivityCallback{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val store = PreferenceDataStore(applicationContext)
        val repo = PreferenceSettingRepository(store)
        runBlocking {
            Log.w("ASDF", store.getString("YEAH", "BOY"))


        }
        /**
        PreferenceUtil.init(getSharedPreferences("Floating", MODE_PRIVATE))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
    */
    }

    private fun checkPermission() {
        //항상 마시멜로 이상
        if (!Settings.canDrawOverlays(this)) {
            //권한이 없을때
            val uri = Uri.fromParts("package", packageName, null) //제발 자바 습관 버리자..
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
            val activityResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (!Settings.canDrawOverlays(this))
                        finish()
                    else
                        supportFragmentManager.beginTransaction().add(R.id.frame, MainFragment())
                            .commit()
                }
            activityResult.launch(intent) //오버레이 그릴 수 있으면 실행 아닌경우 종료

        } else
            supportFragmentManager.beginTransaction().add(R.id.frame, MainFragment()).commit()
    }


    override fun switchSetting() {
        supportFragmentManager.beginTransaction().addToBackStack(null)
            .replace(R.id.frame, SettingFragment()).commit()
    }

    override fun startService(setting : com.comet.floatingtwitter.model.Settings) {
        stopService()
        val intent = Intent(this, FloatingService::class.java)
        intent.putExtra("setting", setting)
        startService(intent)
    }

    override fun stopService() {
        stopService(Intent(this, FloatingService::class.java))
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(1)
    }
}

// logging
fun Any.getClassName() : String = this.javaClass.simpleName