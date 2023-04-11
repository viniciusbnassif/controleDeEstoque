package com.kingdom.controledeestoque

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.view.*
import android.view.View.VISIBLE
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.kingdom.controledeestoque.database.Sync
import kotlinx.coroutines.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class Notificacoes(username: String, context: Context) : Fragment() {
    val username = username
    val contextNav = context
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.activity_notificacoes, container, false).apply {



        var ctxt = getActivity()?.getApplicationContext()


        val toolbar = findViewById<Toolbar>(R.id.topAppBar)


        //val username = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE).toString()
        val cursor = SQLiteHelper(ctxt).getInternalNotificacao(username)
        //var ctxt = this

        var recycleView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewNotificacao)

        var swipe = findViewById<SwipeRefreshLayout>(R.id.swipe)
        swipe.setOnRefreshListener {
            update()
            /*recycleView.adapter = null
            recycleView.layoutManager = null*/
            CoroutineScope(Dispatchers.Unconfined).launch {

                try {
                    ctxt?.let { Sync().sync(2, it) }

                } catch (e: Exception){}
                val cursorUpdate = SQLiteHelper(ctxt).getInternalNotificacao(username)

                MainScope().launch{
                /*MainScope().run {


                    startActivity(getIntent())
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()*/

                    recycleView.adapter = ctxt?.let { RecyclerAdapter(cursorUpdate, contextNav) }
                    recycleView.adapter?.notifyDataSetChanged()
                    recycleView.layoutManager = LinearLayoutManager(ctxt)
                    swipe.isRefreshing = false

                }
            }
        }



        recycleView.adapter = ctxt?.let { RecyclerAdapter(cursor, contextNav) }
        recycleView.layoutManager = LinearLayoutManager(ctxt)
        //recycleView.setHasFixedSize(true)
        recycleView.adapter?.notifyDataSetChanged()

    }
    fun update(){
        super.onDestroy()
        super.onCreate(Bundle())
    }
    /*override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun Options(menu: Menu, inflater: MenuInflater) {
        menuInflater.inflate(R.menu.notificacoes, menu)

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
    }*/

}