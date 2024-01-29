package com.kingdom.controledeestoque

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.Color.WHITE
import android.graphics.Color.parseColor
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.internal.ContextUtils.getActivity
import com.kingdom.controledeestoque.database.getNotificacao

public class RecyclerAdapter(cursorE: Cursor?, context: Context): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    var cursor = cursorE
    var ctxt = context

    public class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.titleID)
        val armzOrigem = itemView.findViewById<TextView>(R.id.armzOrigem)
        val armzDest = itemView.findViewById<TextView>(R.id.armzDest)
        val lote = itemView.findViewById<TextView>(R.id.lote)
        val qtdMov = itemView.findViewById<TextView>(R.id.qtdMovimento)
        val mensagem = itemView.findViewById<TextView>(R.id.mensagem)
        val dty = itemView.findViewById<TextView>(R.id.hora)
        val card = itemView.findViewById<CardView>(R.id.card)
        val read = itemView.findViewById<View>(R.id.read)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_notificacao, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (cursor != null) {
            cursor!!.moveToPosition(position)
            var dados = cursor?.getString(3)?.split("|")?.toTypedArray()
            var msg = cursor?.getString(1)
            var lido = cursor?.getString(5)
            var user = cursor?.getString(4)
            var id = cursor?.getInt(0)


            holder.title.text = dados!![0]
            holder.armzOrigem.text = "Armazem Origem: \n${dados!![1]}"
            holder.armzDest.text = "Armazem Destino: \n${dados!![2]}"
            holder.lote.text = "Lote: \n${dados!![3]}"
            holder.qtdMov.text = "Quantidade: \n${dados!![4]}"
            holder.dty.text = dados!![5]
            holder.mensagem.text = "Mensagem: \n${dados!![6]}"


            if (lido == "N") {
                holder.read.visibility = View.VISIBLE
                holder.card.setCardBackgroundColor(getColor(ctxt, R.color.unreadBackgroundColor))
            } else {
                holder.read.visibility = View.INVISIBLE
                holder.card.setCardBackgroundColor(getColor(ctxt, R.color.containerBackground))
            }

            holder.card.setOnClickListener{
                SQLiteHelper(ctxt).setNotificationRead(id)
                holder.read.visibility = View.INVISIBLE
                holder.card.setCardBackgroundColor(WHITE)
                MaterialAlertDialogBuilder(ctxt)
                    .setTitle("Mensagem detalhada")
                    .setMessage(msg)
                    .setPositiveButton("Fechar") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()

                if (user != null) {

                    Notificacoes(user, ctxt).updateBadge()
                }
            }
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