package com.kingdom.controledeestoque

import android.os.Bundle
import android.content.Context
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import java.lang.Float.parseFloat
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Use the [Requisicao.newInstance] factory method to
 * create an instance of this fragment.
 */
class Requisicao(username: String?, context: Context) : Fragment() {
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
                        var simpleCursorAdapter =
                            ctxt?.let { it1 ->
                                ArrayAdapter<Any>(
                                    it1,
                                    android.R.layout.simple_dropdown_item_1line,
                                    cursorArray
                                )
                            }
                        spinner.setAdapter(simpleCursorAdapter) //spinner recebe os dados para exibição

                        //ao selecionar um item
                        spinner.onItemClickListener =

                            AdapterView.OnItemClickListener { p0, view, position, _id ->
                                if (view?.context != null) {
                                    produtoID.setText(_id.toInt().toString())

                                }
                            }



                    if (ctxt != null) {
                        getActivity()?.let { it1 ->
                            MaterialAlertDialogBuilder(it1)
                                .setView(customAlertDialogView)
                                .setTitle("Criando requisição")
                                .setNegativeButton("Fechar") { dialog, which ->
                                    dialog.dismiss()
                                }
                                .setPositiveButton("Salvar") { dialog, which ->
                                    //TODO
                                }
                                .show()
                        }
                    }
                    /*if (ctxt != null) {
                        val builder: AlertDialog.Builder? =
                            getActivity()
                                ?.let { it1 -> AlertDialog.Builder(it1) }
                        if (builder != null) {
                            builder.setTitle("Name")
                        }

                        // set the custom layout
                        val customLayout: View =
                            layoutInflater.inflate(R.layout.alertdialog_requisicao_step1, null)
                        //builder.setView(customLayout)

                        // add a button
                        if (builder != null) {
                            builder.setPositiveButton("OK") { dialog, which ->
                                // send data from the AlertDialog to the Activity
                                /*val editText = customLayout.findViewById<EditText>(android.R.id.editText)
                                                sendDialogDataToActivity(editText.text.toString())*/
                            }
                        }
                        // create and show the alert dialog
                        builder?.create()?.show()

                    }*/

                }

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