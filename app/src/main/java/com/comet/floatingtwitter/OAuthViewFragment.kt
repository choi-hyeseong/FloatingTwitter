package com.comet.floatingtwitter

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.comet.floatingtwitter.util.PreferenceUtil
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.pkce.PKCE
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod
import com.twitter.clientlib.TwitterCredentialsOAuth2
import com.twitter.clientlib.auth.TwitterOAuth20Service
import kotlinx.coroutines.runBlocking


class OAuthViewFragment : Fragment() {

    private val preference: PreferenceUtil? = PreferenceUtil.INSTANCE
    private lateinit var service: TwitterOAuth20Service
    private lateinit var pkce: PKCE
    private var isRequested = false;

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.oauth_layout, container, false);
        val webView = view.findViewById<WebView>(R.id.webview);
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let {
                    if (!url.contains("oauth2") && url.contains("code=")) {
                        runBlocking {
                            //wait
                            Thread {
                                if (!isRequested) {
                                    isRequested = true;
                                    val code = url.split("code=")[1]
                                    val accessToken = service.getAccessToken(pkce, code)
                                    preference?.putString("token", accessToken.accessToken)
                                    preference?.putString("refresh", accessToken.refreshToken)
                                    requireActivity().runOnUiThread{
                                        Toast.makeText(
                                            context,
                                            "토큰이 등록되었습니다. 뒤로가기를 눌러주세요.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }.start()
                        }

                    }
                }
            }
        }
        executeOAuth(webView)
        return view;
    }

    private fun executeOAuth(webView: WebView) {
        val credentials = TwitterCredentialsOAuth2(
            BuildConfig.CLIENT_ID,
            BuildConfig.CLIENT_SECRET,
            BuildConfig.ACCESS_TOKEN,
            BuildConfig.API_KEY
        )
        service = TwitterOAuth20Service(
            credentials.twitterOauth2ClientId,
            credentials.twitterOAuth2ClientSecret,
            "http://twitter.com",
            "offline.access tweet.read users.read dm.read"
        )
        var token: OAuth2AccessToken? = null
        val secretState = "state"
        pkce = PKCE()
        pkce.codeChallenge = "challenge"
        pkce.codeChallengeMethod = PKCECodeChallengeMethod.PLAIN
        pkce.codeVerifier = "challenge"
        val authorizationUrl = service.getAuthorizationUrl(pkce, secretState)
        webView.loadUrl(authorizationUrl)
    }

}