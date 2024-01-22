package com.kingdom.controledeestoque

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.kingdom.controledeestoque.database.Sync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import java.util.Date

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

        var btnCrtReq = findViewById<ExtendedFloatingActionButton>(R.id.createReq)
        btnCrtReq.setOnClickListener{
            var customAlertDialogView : View = layoutInflater.inflate(R.layout.alertdialog_requisicao_step1, null)

            var spinner = customAlertDialogView.findViewById<AutoCompleteTextView>(R.id.spinnerPrd)
            var produtoID = customAlertDialogView.findViewById<MaterialTextView>(R.id.spinnerIdSaver)

            var qtd = customAlertDialogView.findViewById<TextInputEditText>(R.id.qtd)

            var soma = customAlertDialogView.findViewById<MaterialButton>(R.id.soma1)
            var subt = customAlertDialogView.findViewById<MaterialButton>(R.id.subt1)
            var buttonToTop = findViewById<MaterialButton>(R.id.backToTop)


            soma.setOnClickListener {
                var empty : Boolean
                var qtdContent = qtd.text.toString()
                if (qtdContent == ""){
                    empty = true
                } else {
                    empty = false
                }
                var floatQtd = parseFloat(qtd.getText().toString())
                if (empty){
                    qtd.setText("0")
                } else {
                    floatQtd += 1
                    qtd.setText((floatQtd).toString())
                }
            }
            subt.setOnClickListener {
                var empty : Boolean
                var qtdContent = qtd.text.toString()
                if (qtdContent == "" || qtdContent =="0"){
                    empty = true
                } else {
                    empty = false
                }
                var floatQtd = parseFloat(qtd.getText().toString())
                if (empty){
                    qtd.setText("0")
                } else {
                    floatQtd -= 1
                    qtd.setText((floatQtd).toString())
                }
            }

            var idintern: Int
            var cursorProduto = db.getProdutos()
            var cursorArray = ArrayList<Any>()
            while (cursorProduto!!.moveToNext()) {
                cursorArray.add(cursorProduto!!.getString(1))
            }
            var simpleCursorAdapter = ArrayAdapter<Any>(contextNav,
                android.R.layout.simple_dropdown_item_1line,
                cursorArray
            )

            spinner.setAdapter(simpleCursorAdapter) //spinner recebe os dados para exibição


            //ao selecionar um item
            spinner.onItemClickListener =

                AdapterView.OnItemClickListener { p0, view, position, _id ->
                    if (view?.context != null) {
                        produtoID.setText(_id.toInt().toString())

                    }
                }
            var dateFormatter = SimpleDateFormat()
            var dty = ""

            var dateFormatter0 = SimpleDateFormat()
            var time = ""

            //var time0 = findViewById<TextView>(R.id.editTextHora)
            //var dty0 = findViewById<TextView>(R.id.editTextData)

            var dateFormatterProtheus = SimpleDateFormat()
            var dtyProtheus = ""


            fun date(): String {

                dateFormatter = SimpleDateFormat("dd/MM/yyyy") //formatar data no formato padrão
                dty = dateFormatter.format(Date())

                dateFormatter0 = SimpleDateFormat("kk:mm") //formatar tempo no formato 24h (kk)
                time = dateFormatter0.format(Date())

                //time0.setText(time)
                //dty0.setText(dty)


                dateFormatterProtheus =
                    SimpleDateFormat("yyyyMMdd") //formatar data no formato padrão
                dtyProtheus = dateFormatterProtheus.format(Date())
                var dtytime0 = "$dtyProtheus" + "$time"

                return dtytime0
            }

            if (ctxt != null) {
                activity?.let { it1 ->
                    MaterialAlertDialogBuilder(it1)
                        .setView(customAlertDialogView)
                        .setTitle("Criando requisição")
                        .setNegativeButton("Fechar") { dialog, which ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Salvar") { dialog, which ->
                            //if (qtd.text.toString() != "0") {
                            var cod = db.getCodRealProd(parseInt(produtoID.text.toString()))
                            var codF = cod!!.getString(1)
                            var query = "INSERT INTO Requisicao (codProduto, qtdRequisicao, userRequisicao," +
                                    "dataHoraRequisicao, statusSync)" +
                                    "VALUES ('${codF}', ${parseFloat(qtd.text.toString())}, '$username', '${date()}', 0)"
                            db.externalExecSQL(query)
                            //(requireActivity() as com.liderMinas.PCP.MainNav).restartFragmentReq()
                            //enviarReqParaServer(produtoID, qtd)
                            buttonToTop.performClick()

                            //}
                        }
                        .show()
                }
            }


        }
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