package com.kingdom.controledeestoque

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.BundleCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.kingdom.controledeestoque.database.Sync
import kotlinx.coroutines.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlin.coroutines.coroutineContext

class Notificacoes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificacoes)

        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowCustomEnabled(false)

        }

        val username = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE).toString()
        val cursor = SQLiteHelper(this).getInternalNotificacao(username)
        var ctxt = this


        var swipe = findViewById<SwipeRefreshLayout>(R.id.swipe)
        swipe.setOnRefreshListener {
            CoroutineScope(Dispatchers.Unconfined).launch {
                try {
                    Sync().sync(2, ctxt)
                } catch (e: Exception){}
                MainScope().run {

                    startActivity(getIntent())
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
            }
        }


        var recycleView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewNotificacao)
        recycleView.adapter = RecyclerAdapter(cursor, this)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.setHasFixedSize(true)

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notificacoes, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {

        R.id.refresh -> {
            // User chose the "Settings" item, show the app settings UI...
            var ctxt = this
            var swipe = findViewById<SwipeRefreshLayout>(R.id.swipe)
            swipe.isRefreshing = true
            findViewById<LinearProgressIndicator>(R.id.progressToolbar).visibility = VISIBLE
            CoroutineScope(Dispatchers.Unconfined).launch {
                try {
                    Sync().sync(2, ctxt)
                } catch (e: Exception){}
                MainScope().run {
                    finish()
                    startActivity(getIntent())
                }
            }



            true
        }


        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}