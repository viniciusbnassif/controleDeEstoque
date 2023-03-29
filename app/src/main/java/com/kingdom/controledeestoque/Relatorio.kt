package com.kingdom.controledeestoque

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Relatorio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relatorio)


        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            //
            systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        val progress = findViewById<CircularProgressIndicator>(R.id.pIndicator)
        progress.visibility = View.VISIBLE

        val myWebView: WebView = findViewById(R.id.webView)
        myWebView.webViewClient = WebViewClient()
        myWebView.clearCache(true)
        myWebView.clearHistory()
        myWebView.settings.javaScriptEnabled = true

        GlobalScope.launch {
            try{
                myWebView.loadUrl("http://192.168.1.10/report_server/Pages/ReportViewer.aspx?%2fLiderMinas%2fRLM0077&rs:Command=Render")
            } catch (e: Exception){}
            progress.visibility = View.INVISIBLE
        }


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowCustomEnabled(false)

        }
        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.ic_baseline_logout_24)

        var refresh = findViewById<ImageView>(R.id.refresh)

        refresh.setOnClickListener{
                    myWebView.reload()


        }

        var close = findViewById<ImageView>(R.id.close)
        close.setOnClickListener{
            finish()
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