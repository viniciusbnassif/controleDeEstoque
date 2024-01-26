package com.kingdom.controledeestoque

import android.content.Context
import android.database.Cursor
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.database.getFloatOrNull
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.InternalCoroutinesApi
import java.lang.Float.parseFloat
import java.util.Date

//import com.google.android.material.internal.ContextUtils.getActivity
//import com.kingdom.controledeestoque.database.getNotificacao

public class RecyclerAdapterReq(cursorE: Cursor?, context: Context): RecyclerView.Adapter<RecyclerAdapterReq.ViewHolder>() {
    var cursor = cursorE
    var ctxt = context
    var db = SQLiteHelper(ctxt)
    @OptIn(InternalCoroutinesApi::class)


    public class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val id = itemView.findViewById<TextView>(R.id.ID)
        val title = itemView.findViewById<TextView>(R.id.titleID)
        val qtdReq = itemView.findViewById<TextView>(R.id.qtdReq)
        val qtdAtend = itemView.findViewById<TextView>(R.id.qtdAtend)
        val userReq = itemView.findViewById<TextView>(R.id.userReq)
        val userAtend = itemView.findViewById<TextView>(R.id.userAtend)
        val dty = itemView.findViewById<TextView>(R.id.hora)
        val card = itemView.findViewById<CardView>(R.id.card)
        val read = itemView.findViewById<View>(R.id.read)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_requisicao, parent, false)


        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        if (cursor != null) {
            cursor!!.moveToPosition(position)
            //var dados = cursor?.getString(3)?.split("|")?.toTypedArray()
            //var dados = ArrayList<String>()
            //while (cursor!!.moveToNext()) {
            //dados.add(getStringFromCursor(cursor))
            //}
            //var msg = cursor?.getString(1)
            //var lido = cursor?.getString(5)
            //var user = cursor?.getString(4)
            var id = cursor?.getInt(0)
            var cod = cursor?.getString(1)
            var qtdReq = cursor?.getFloat(2)
            var qtdAtend = cursor?.getFloatOrNull(3)
            var userReq = cursor?.getString(5)
            var userAtend = cursor?.getStringOrNull(6)
            var dty = cursor?.getString(8)

            Log.d("id","${id}")
            Log.d("cod","${cod}")
            Log.d("qtdReq","${qtdReq}")
            Log.d("qtdAtend","${qtdAtend}")

            var dateFormatted = "${dty.toString().substring(8,10)}:${dty.toString().substring(11,13)} ${dty.toString().substring(6,8)}/${dty.toString().substring(4,6)}/${dty.toString().substring(0,4)}"

            var descprod= cod?.let { db.getDescProdutosEst(it) }
            var prodNome = "${
                descprod?.getString(1)?.substring(18)}"
            Log.d("descprod","${descprod}")
            Log.d("prodNome","${prodNome}")

            holder.id.text = "$id"
            holder.title.text = "${prodNome}"
            holder.qtdReq.text = "Qtd solicitada: ${qtdReq.toString()}"
            holder.qtdReq.hint = "${qtdReq.toString()}"
            if (qtdAtend !=null) {
                holder.qtdAtend.text = "Qtd enviada: ${qtdAtend?.toString() ?: ""}"
            } else {
                holder.qtdAtend.text = "Qtd enviada: Aguardando resposta"
            }
            holder.userReq.text = "Solicitado por ${userReq.toString()}"
            if (userAtend !=null) {
                holder.userAtend.text = "Respondido por: ${userAtend?.toString() ?: ""}"
            } else {
                holder.userAtend.
                text = "Respondido por: Aguardando resposta"
            }
            //holder.userAtend.text = "Respondido por: ${userAtend?.toString() ?: ""}"//if (userAtend) {userAtend.toString()} else {""}
            holder.dty.text = dateFormatted

            var dateFormatter = SimpleDateFormat()


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


            holder.card.setOnClickListener {


                //SQLiteHelper(ctxt).setNotificationRead(id)
                //holder.read.visibility = View.INVISIBLE
                //holder.card.setCardBackgroundColor(WHITE)

                var dialogBuilder = MaterialAlertDialogBuilder(ctxt)
                    .setTitle("Solicitação $id")
                    .setView(R.layout.alertdialog_requisicao_step2)
                    //.setMessage(msg)
                    //.setMessage("Digite a quantidade recebida e clique em Salvar")
                    .show()

                var prodName =
                    dialogBuilder.findViewById<TextInputEditText>(R.id.editTextProdutoName)
                        ?.setText("${holder.title.text}")
                Log.d("holder.title.text","${holder.title.text}")
                dialogBuilder.findViewById<TextInputEditText>(R.id.editTextProdutoName)
                    ?.setHint("Produto")
                dialogBuilder.findViewById<TextInputEditText>(R.id.editTextProdutoName)?.isEnabled =
                    false
                dialogBuilder.findViewById<MaterialButton>(R.id.fechar)?.setOnClickListener { dialogBuilder.dismiss() }

                Log.d("holder.qtdAtend.hint","${holder.qtdAtend.hint}")

                var qtd = dialogBuilder.findViewById<TextInputEditText>(R.id.qtd)
                qtd?.setText("${holder.qtdAtend.hint}")

                var txt = "null"
                    var soma1 =
                        dialogBuilder.findViewById<MaterialButton>(R.id.soma1)?.setOnClickListener {
                            var qtdS = parseFloat(qtd?.text.toString())
                            qtdS += 1
                            qtd?.setText("$qtdS")
                        }
                    var subt1 =
                        dialogBuilder.findViewById<MaterialButton>(R.id.subt1)?.setOnClickListener {
                            var qtdS = parseFloat(qtd?.text.toString())
                            qtdS -= 1
                            qtd?.setText("$qtdS")
                        }
                    dialogBuilder.findViewById<TextView>(R.id.nomePasso)?.setText("Digite a quantidade que será enviada e clique em Salvar")
                    dialogBuilder.findViewById<MaterialButton>(R.id.subt1)?.visibility = VISIBLE
                    dialogBuilder.findViewById<MaterialButton>(R.id.soma1)?.visibility = VISIBLE
                    dialogBuilder.findViewById<TextInputEditText>(R.id.qtd)?.visibility = VISIBLE
                    dialogBuilder.findViewById<TextInputEditText>(R.id.qtd)?.setText("0.0")
                    dialogBuilder.findViewById<MaterialButton>(R.id.salvar)?.setOnClickListener {
                        var query = """
                                UPDATE Requisicao
                                SET qtdAtendida = ${parseFloat(qtd?.text.toString())}, userAtendimento = '${userReq.toString()}', dataHoraAtendimento = '${date()}', statusSync = 2
                                WHERE idRequisicao = ${holder.id.text} AND qtdRequisicao = ${qtdReq.toString()} AND userRequisicao = '${userReq.toString()}';
                            """.trimIndent()
                        db.externalExecSQL(query)
                        dialogBuilder.dismiss()
                        holder.card.setOnClickListener {
                            MaterialAlertDialogBuilder(ctxt)
                                .setTitle("Finalizado")
                                .setMessage("Essa transferência foi finalizada. Não há mais nada a fazer por aqui.")
                                .setPositiveButton("Fechar") { dialog, which ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                        holder.title.setText("Finalizado")
                        holder.card.setCardBackgroundColor(ContextCompat.getColor(ctxt, R.color.suErrorRed))
                    }



            }




            //if (user != null) {

            //Notificacoes(user, ctxt).updateBadge()
            //}

            //cursor!!.moveToNext()
        }

    }

    override fun getItemCount(): Int {
        if (cursor != null){
            var rtn = cursor!!.count
            return rtn
        }
        return 0


    }

}