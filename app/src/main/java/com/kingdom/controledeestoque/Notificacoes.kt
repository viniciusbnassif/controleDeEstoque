package com.kingdom.controledeestoque

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kingdom.controledeestoque.database.Sync
import kotlinx.coroutines.*
import org.w3c.dom.Text

class Notificacoes(username: String, context: Context) : Fragment() {
    var username = username
    val contextNav = context
    var db = SQLiteHelper(contextNav)
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.activity_notificacoes, container, false).apply {



        var ctxt = getActivity()?.getApplicationContext()



        val toolbar = findViewById<Toolbar>(R.id.topAppBar)


        //val username = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE).toString()
        val cursor = username?.let { SQLiteHelper(ctxt).getInternalNotificacao(it) }
        //var ctxt = this

        fun updateBadge(){ //Esse metodo atualiza o contador na barra de navegação
            var cursor = username?.let { SQLiteHelper(contextNav).countNotf(it) } //Conta quantas notificações não lidas existem para o usuario atual
            if (cursor != null) { //Por segurança, se o resultado for nulo (muito dificil) ele não fará nada.
                val activity: Activity? = activity //Instancia a atividade
                if (activity is Main_nav) { //confirma se a atividade foi instanciada
                    val myactivity: Main_nav? = activity as Main_nav?
                    myactivity?.getCount(cursor) //executa o metodo que atualiza o contador, passando o numero obtido pelo cursor na primeira linha da função.
                }
            }

        }

        var aviso = findViewById<TextView>(R.id.aviso)


        var recycleView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewNotificacao)

        var swipe = findViewById<SwipeRefreshLayout>(R.id.swipe)
        swipe.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimaryVariant,
            R.color.colorSecondary)

        swipe.setOnRefreshListener {
            update()
            updateBadge()
            /*recycleView.adapter = null
            recycleView.layoutManager = null*/
            CoroutineScope(Dispatchers.Unconfined).launch {

                try {
                    ctxt?.let { Sync().sync(2, it) }

                } catch (e: Exception){}
                val cursorUpdate = username?.let { SQLiteHelper(ctxt).getInternalNotificacao(it) }

                MainScope().launch{
                /*MainScope().run {


                    startActivity(getIntent())
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()*/

                    recycleView.adapter = RecyclerAdapter(cursorUpdate, contextNav)
                    recycleView.adapter?.notifyDataSetChanged()
                    recycleView.layoutManager = LinearLayoutManager(ctxt)
                    swipe.isRefreshing = false


                    if (cursorUpdate == null || cursorUpdate.count == 0){
                        aviso.setVisibility(View.VISIBLE)
                    }
                    else{
                        aviso.setVisibility(View.GONE)
                    }
                }
            }
        }




        recycleView.adapter = ctxt?.let { RecyclerAdapter(cursor, contextNav) }
        recycleView.layoutManager = LinearLayoutManager(ctxt)
        //recycleView.setHasFixedSize(true)
        recycleView.adapter?.notifyDataSetChanged()

        if (cursor == null || cursor.count == 0){
            aviso.setVisibility(View.VISIBLE)
        }
        else{
            aviso.setVisibility(View.GONE)
        }


    }
    fun updateBadge(){ //Esse metodo atualiza o contador na barra de navegação

        var cursor = username?.let { db.countNotf(it) } //Conta quantas notificações não lidas existem para o usuario atual
        if (cursor != null) { //Por segurança, se o resultado for nulo (muito dificil) ele não fará nada.
            val activity: FragmentActivity? = activity
            if (activity != null && activity is Main_nav) {
                val myactivity: Main_nav = activity as Main_nav
                myactivity.getCount(cursor)
            }
        }
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