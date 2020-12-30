package com.ice.covidalert.web

import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient

class CustomWebViewClient(
    private var baseContentLink: String
) : WebViewClient() {

    private var baseHost: String = Uri.parse(baseContentLink).host.toString()

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        // allow to launch link that located in our host area
        return Uri.parse(url).host != baseHost
        // launch browser if redirect to other site
//        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
//            startActivity(this)
//        }
    }
}