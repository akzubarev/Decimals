package com.education4all.activities

import android.annotation.TargetApi
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.education4all.R

class WebActivity : AppCompatActivity() {
    private var webView: WebView? = null
    private var connected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connected = CheckForConnection()
        if (connected) {
            setContentView(R.layout.web)
            webView = findViewById(R.id.webView)
            webView.getSettings().javaScriptEnabled = true
            webView.setWebViewClient(MyWebViewClient())
            webView.loadUrl("http://math-trainer.ru")
        } else {
            setContentView(R.layout.no_internet)
            //            TextView messagetext = findViewById(R.id.message);
//            messagetext.setText(message);
        }
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun CheckForConnection(): Boolean {
        var internetConnection = false
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            message = "Build version > M";
//            Network nw = connectivityManager.getActiveNetwork();
//            if (nw == null) {
//                internetConnection = false;
//                message += ", nw==null";
//            } else {
//                NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
//                internetConnection = actNw != null && (
//                        actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
//                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
//                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
//                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)// ||
//                    //    actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
//                );
//
//                message += actNw == null ? ", actNw==null" : ", no transport";
//            }
        //      } else {
        var message = "Build version < M"
        val nwInfo = connectivityManager.activeNetworkInfo
        internetConnection = nwInfo != null && nwInfo.isConnected
        message += if (nwInfo == null) ", nwInfo==null" else ", nw is not connected"
        //        }
        return internetConnection
    }

    override fun onBackPressed() {
        if (connected && webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private class MyWebViewClient : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString())
            return true
        }

        // Для старых устройств
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }
}