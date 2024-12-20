package com.comet.floatingtwitter

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.comet.floatingtwitter.callback.ActivityCallback
import com.comet.floatingtwitter.overlay.service.FloatingService
import com.comet.floatingtwitter.twitter.oauth.view.OAuthViewFragment
import com.comet.floatingtwitter.twitter.setting.view.SettingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityCallback {

    private val activityManager : ActivityManager by lazy { getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }

    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Settings.canDrawOverlays(this)) {
            init()
        }
        else {
            Toast.makeText(this, getString(R.string.require_permission), Toast.LENGTH_LONG).show()
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        /**
        PreferenceUtil.init(getSharedPreferences("Floating", MODE_PRIVATE))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
         */
    }

    private fun checkPermission() {
        if (!Settings.canDrawOverlays(this)) // 오버레이를 못그러면 펄미션 요청
            requestPermission()
        else init()
    }

    private fun requestPermission() {
        // permission X not working
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        activityResultLauncher.launch(intent)
    }

    private fun init() {
        supportFragmentManager.popBackStack() // backstack이 없어도 문제 없음 (그냥 미작동) < switch main 전용 (뒤로가기 방지)
        supportFragmentManager.beginTransaction().replace(R.id.frame, MainFragment()).commit()
    }

    override fun switchMain() {
        init()
    }

    override fun switchOAuth() {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.frame, OAuthViewFragment())
            .commit()
    }

    override fun switchSetting() {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.frame, SettingFragment())
            .commit()
    }

    override fun startService() {
        if (isServiceRunning(FloatingService::class.java))
            throw IllegalStateException("이미 실행중인 서비스입니다.")
        startService(Intent(this, FloatingService::class.java))
    }

    override fun stopService() {
        stopService(Intent(this, FloatingService::class.java))
    }

    // 서비스 동작 확인
    // deprecated이긴 하지만 일단 사용하고 가능하면 isRunning 쓰라고는 하는데 음...
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        return activityManager.getRunningServices(Int.MAX_VALUE).find { it.service.className == serviceClass.name } != null

    }
}

// logging
fun Any.getClassName(): String = this.javaClass.simpleName

//뒤로가기 금지하고 액티비티 시작하는 확장함수
fun Activity.startActivityWithBackstackClear(targetClass : Class<*>) {
    startActivity(Intent(this, targetClass).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)))
}