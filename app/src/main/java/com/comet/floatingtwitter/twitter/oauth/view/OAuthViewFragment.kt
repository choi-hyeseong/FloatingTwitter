package com.comet.floatingtwitter.twitter.oauth.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.comet.floatingtwitter.databinding.OauthLayoutBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OAuthViewFragment : Fragment() {

    private val oAuthFragmentViewModel: OAuthFragmentViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = OauthLayoutBinding.inflate(layoutInflater)
        initWebView(binding.webview)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(webView: WebView) {
        // js 허용
        webView.settings.javaScriptEnabled = true
        oAuthFragmentViewModel.urlLiveData.observe(viewLifecycleOwner) {
            webView.loadUrl(it)
        }
    }

}