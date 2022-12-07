package com.comet.floatingtwitter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment

class OAuthViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.oauth_layout, container, false);
        val webView = view.findViewById<WebView>(R.id.webview);
        webView.loadUrl("https://twitter.com");
        return view;
    }

}