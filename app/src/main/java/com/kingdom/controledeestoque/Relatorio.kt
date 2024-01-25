package com.kingdom.controledeestoque

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Relatorio : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.activity_relatorio, container, false).apply {


        val myWebView: WebView = findViewById(R.id.webView)
        myWebView.webViewClient = WebViewClient()
        myWebView.clearCache(true)
        myWebView.clearHistory()
        myWebView.settings.setSupportZoom(true)
        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.loadWithOverviewMode = true
        myWebView.loadUrl("http://192.168.1.10/report_server/Pages/ReportViewer.aspx?%2fLiderMinas%2fRLM0078&rs:Command=Render")



        var refresh = findViewById<ImageView>(R.id.refresh)

        refresh.setOnClickListener{
            myWebView.reload()
        }


        /*var contentContainer = findViewById<TableView>(R.id.content_container)

        var ch = "test"
        var rh = "test"
        var c = "test"

        var list =


        var adapter = contentContainer.setAdapter()

        contentContainer.setAdapter(adapter)*/

    }
}