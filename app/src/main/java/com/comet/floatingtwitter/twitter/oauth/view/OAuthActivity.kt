package com.comet.floatingtwitter.twitter.oauth.view

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.comet.floatingtwitter.MainActivity
import com.comet.floatingtwitter.R
import com.comet.floatingtwitter.getClassName
import com.comet.floatingtwitter.startActivityWithBackstackClear
import dagger.hilt.android.AndroidEntryPoint

/**
 * Deep Link를 통해 구현된 oauth activity
 */
@AndroidEntryPoint
class OAuthActivity : AppCompatActivity() {

    private val oAuthViewModel: OAuthViewModel by viewModels()

    // onCreate시 param 2개짜리는 왠만하면 사용 안함
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initIntent(intent)
        initObserver()
    }

    private fun initObserver() {
        oAuthViewModel.responseLiveData.observe(this) {
            it.getContent()?.let { isSuccess ->
                if (isSuccess)
                    Toast.makeText(this, R.string.oauth_set, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, R.string.oauth_error, Toast.LENGTH_LONG).show()
                startActivityWithBackstackClear(MainActivity::class.java)
            }
        }
    }

    private fun initIntent(intent: Intent) {
        val uri = intent.data
        if (uri == null) {
            Toast.makeText(this, R.string.invalid_intent, Toast.LENGTH_LONG).show()
            finish()
        }
        else {
            // ftweet://auth?code=~~~~~"
            val code = uri.getQueryParameter("code")
            if (code == null) {
                // invalid
                Toast.makeText(this, R.string.invalid_intent, Toast.LENGTH_LONG).show()
                finish()
            }
            else oAuthViewModel.login(code)
        }
    }
}