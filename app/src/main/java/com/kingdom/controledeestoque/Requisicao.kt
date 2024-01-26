package com.kingdom.controledeestoque

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.kingdom.controledeestoque.database.Sync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Use the [Requisicao.newInstance] factory method to
 * create an instance of this fragment.
 */
class Requisicao(username: String, context: Context) : Fragment() {
    var username = username
    val contextNav = context
    var db = SQLiteHelper(contextNav)
    // TODO: Rename and change types of parameters
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_requisicao, container, false).apply {
        var ctxt = getActivity()?.getApplicationContext()
        var db = SQLiteHelper(ctxt)

        var recycleView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewRequisicao)

        var aviso = findViewById<TextView>(R.id.aviso)

        var swipe = findViewById<SwipeRefreshLayout>(R.id.swipe)
        swipe.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimaryVariant,
            R.color.colorSecondary)
        //fun backToTop() {swipe.setScrollY(0)}
        var buttonToTop = findViewById<MaterialButton>(R.id.backToTop)
        buttonToTop.setOnClickListener {
            val cursorUpdate = SQLiteHelper(ctxt).getInternalRequisicao()

            MainScope().launch {
                /*MainScope().run {


                    startActivity(getIntent())
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()*/

                recycleView.adapter = RecyclerAdapterReq(cursorUpdate, contextNav)
                recycleView.adapter?.notifyDataSetChanged()
                recycleView.layoutManager = LinearLayoutManager(ctxt)
                swipe.isRefreshing = false


                if (cursorUpdate == null || cursorUpdate.count == 0) {
                    aviso.setVisibility(View.VISIBLE)
                } else {
                    aviso.setVisibility(View.GONE)
                }
            }
        }
        buttonToTop.performClick()
        swipe.setOnRefreshListener {
            update()
            updateBadge()
            /*recycleView.adapter = null
            recycleView.layoutManager = null*/
            CoroutineScope(Dispatchers.Unconfined).launch {

                try {
                    ctxt?.let { Sync().sync(2, it) }

                } catch (e: Exception){}
                val cursorUpdate = SQLiteHelper(ctxt).getInternalRequisicao()

                MainScope().launch{
                    /*MainScope().run {


                        startActivity(getIntent())
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()*/

                    recycleView.adapter = RecyclerAdapterReq(cursorUpdate, contextNav)
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
        fun forceUpdate(){
            val cursorUpdate = SQLiteHelper(ctxt).getInternalRequisicao()
            recycleView.adapter = RecyclerAdapterReq(cursorUpdate, contextNav)
            recycleView.adapter?.notifyDataSetChanged()
            recycleView.layoutManager = LinearLayoutManager(ctxt)
            swipe.isRefreshing = false
        }
        forceUpdate()




    }
    fun updateBadge(){ //Esse metodo atualiza o contador na barra de navegação

        var cursor = username?.let { db.countReqs(it) } //Conta quantas notificações não lidas existem para o usuario atual
        if (cursor != null) { //Por segurança, se o resultado for nulo (muito dificil) ele não fará nada.
            val activity: FragmentActivity? = activity
            if (activity != null && activity is Main_nav) {
                val myactivity: Main_nav = activity as Main_nav
                //myactivity.getCount(cursor)
            }
        }
    }

    fun update(){
        super.onDestroy()
        super.onCreate(Bundle())
    }



    /*companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Requisicao.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Requisicao().apply {
                arguments = Bundle().apply {

                }
            }
    }*/
}