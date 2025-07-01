package com.kingdom.controledeestoque

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.*
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kingdom.controledeestoque.database.Sync
import kotlinx.coroutines.*
import org.w3c.dom.Text

class Notificacoes() : AppCompatActivity() {
    val contextNav = this
    private lateinit var prefs: PreferencesHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificacoes)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun isLightTheme(): Boolean {
            return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO
        }
        // Verifique se está no modo claro ou escuro
        if (isLightTheme()) {
            // Ícones escuros (modo claro)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            // Ícones claros (modo escuro)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        prefs = PreferencesHelper(applicationContext)

        var ctxt = applicationContext
        val username = prefs.getData("username", "Guest")

        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar) // Configura a Toolbar como barra de ação
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Fecha a Activity ou volta na pilha de navegação
        }

        //val username = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE).toString()
        val cursor = SQLiteHelper(ctxt).getInternalNotificacao(username)
        //var ctxt = this

        /*fun updateBadge(){ //Esse metodo atualiza o contador na barra de navegação
            var cursor = username?.let { SQLiteHelper(contextNav).countNotf(it) } //Conta quantas notificações não lidas existem para o usuario atual
            if (cursor != null) { //Por segurança, se o resultado for nulo (muito dificil) ele não fará nada.
                // Instancia a atividade
                if (activity is Main_nav) { //confirma se a atividade foi instanciada
                    val myactivity: Main_nav? = activity as Main_nav?
                    myactivity?.getCount(cursor) //executa o metodo que atualiza o contador, passando o numero obtido pelo cursor na primeira linha da função.
                }
            }

        }*/

        var aviso = findViewById<TextView>(R.id.aviso)

        var recycleView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewNotificacao)

        //var swipe = findViewById<ScrollView>(R.id.swipe)
        /*swipe.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimaryVariant,
            R.color.colorSecondary)

        swipe.setOnRefreshListener {
            update()
            //-updateBadge()
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
                    //swipe.isRefreshing = false


                    if (cursorUpdate == null || cursorUpdate.count == 0){
                        aviso.setVisibility(View.VISIBLE)
                    }
                    else{
                        aviso.setVisibility(View.GONE)
                    }
                }
            }
        }*/




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
    /*fun updateBadge(){ //Esse metodo atualiza o contador na barra de navegação

        var cursor = username?.let { db.countNotf(it) } //Conta quantas notificações não lidas existem para o usuario atual
        if (cursor != null) { //Por segurança, se o resultado for nulo (muito dificil) ele não fará nada.
            val activity: FragmentActivity? = this
            if (activity != null && activity is Main_nav) {
                val myactivity: Main_nav = activity as Main_nav
                myactivity.getCount(cursor)
            }
        }
    }*/
    fun update(){
        super.onDestroy()
        super.onCreate(Bundle())
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}