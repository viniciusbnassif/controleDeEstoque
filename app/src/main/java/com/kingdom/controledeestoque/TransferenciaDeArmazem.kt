package com.kingdom.controledeestoque

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.text.TextUtils.substring
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kingdom.controledeestoque.SQLiteHelper
import com.kingdom.controledeestoque.database.Connection
import java.lang.Float.parseFloat

class TransferenciaDeArmazem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transferencia_de_armazem)

        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        val toolbar = findViewById<Toolbar>(R.id.bottomAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowCustomEnabled(false)

        }
        var db = SQLiteHelper(this)

        val username = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)

        var idArmzOrig: String
        var idPrd: String
        var idLote: String
        var idMovimento: String
        var idArmzDest: String



        var spinnerCodArmzOrig = findViewById<TextView>(R.id.spinnerCodArmzOrig)
        var spnArmzOrig = findViewById<AutoCompleteTextView>(R.id.spinnerArmzOrigem)


        var viewSpinnerPrd = findViewById<TextInputLayout>(R.id.viewSpinner)

        var spinnerCodPrd = findViewById<TextView>(R.id.spinnerCodPrd)
        var spnPrd = findViewById<AutoCompleteTextView>(R.id.spinnerPrd)

        var spinnerCodLote = findViewById<TextView>(R.id.spinnerCodPrd)
        var viewSpnLote = findViewById<TextInputLayout>(R.id.viewSpinnerLote)
        var spnLote = findViewById<AutoCompleteTextView>(R.id.spinnerLote)

        var movimento = findViewById<TextInputEditText>(R.id.qtdMovimento)

        var spinnerCodArmzDest = findViewById<TextView>(R.id.spinnerCodArmzDest)
        var spnArmzDest = findViewById<AutoCompleteTextView>(R.id.spinnerArmzDest)

        var rastro: Boolean


        fun saldoProduto(){
            var codArmzOrig = spinnerCodArmzOrig.text.toString()
            var codPrd = spinnerCodPrd.text.toString()
            if (codArmzOrig.length > 0 && codPrd.length > 0) {
                var cursorSaldo = db.getSaldo(codArmzOrig, codPrd)
                if (cursorSaldo != null) {
                    cursorSaldo.moveToFirst()
                    val saldo = cursorSaldo.getFloat(1).toString()
                    viewSpinnerPrd.helperText = "Saldo: $saldo"
                }
            }
        }

        fun setOrRefreshSpnLote() {
            spnLote.setText("")
            var codArmzOrig = spinnerCodArmzOrig.text.toString()
            //var cursorArmzOg = db.getCodArmz(idArmzOrig as String)
            var codPrd = spinnerCodPrd.text.toString()
            //var cursorPrd = db.getCodPrd(idPrd as String)
            if (codArmzOrig.length > 0 && codPrd.length > 0) {
                var cursorLote = db.getLote("$codArmzOrig", "$codPrd")

                var cursorArray = ArrayList<Any>()
                if (cursorLote != null) {
                    cursorLote.moveToFirst()
                    cursorArray.add(cursorLote.getString(1) + " - Saldo: " + "${cursorLote.getFloat(2)}")
                    while (cursorLote.moveToNext()) {
                        cursorArray.add(cursorLote.getString(1) + " - Saldo: " + "${cursorLote.getFloat(2)}")
                    }
                }
                var simpleCursorAdapter =
                    ArrayAdapter<Any>(this, android.R.layout.simple_dropdown_item_1line, cursorArray)
                spnLote.setAdapter(simpleCursorAdapter) //spinner recebe os dados para exibição
            }

            rastro = db.getRastro("$codPrd")
            if (rastro == true) {
                viewSpnLote.error = getString(R.string.campo_obrigatorio)
                viewSpnLote.isEnabled = true

            } else if (rastro == false){
                viewSpnLote.error = ""
                viewSpnLote.isErrorEnabled = false
                viewSpnLote.isEnabled = false

            }

            //ao selecionar um item
            spnLote.onItemClickListener =
                AdapterView.OnItemClickListener { p0, view, position, _id ->
                    if (view?.context != null) {
                        idLote = _id.toInt().toString()
                    }
                    spinnerCodLote.text = spnLote.text.substring(0,10)
                    saldoProduto()
                }
        }

        fun setOrRefreshSpnArmzOrig() {

            var cursorArmzOrig = db.getArmz()
            var cursorArray = ArrayList<Any>()
            if (cursorArmzOrig != null) {
                cursorArmzOrig.moveToFirst()
                cursorArray.add(cursorArmzOrig.getString(1))
                while (cursorArmzOrig.moveToNext()) {
                    cursorArray.add(
                        cursorArmzOrig.getInt(0),
                        cursorArmzOrig.getString(1) + " - " + cursorArmzOrig.getString(2)
                    )
                }
            }
            var simpleCursorAdapter =
                ArrayAdapter<Any>(this, android.R.layout.simple_dropdown_item_1line, cursorArray)
            spnArmzOrig.setAdapter(simpleCursorAdapter) //spinner recebe os dados para exibição

            //ao selecionar um item
            spnArmzOrig.onItemClickListener =
                AdapterView.OnItemClickListener { p0, view, position, _id ->
                    if (view?.context != null) {
                        spinnerCodArmzOrig.text = spnArmzOrig.text.substring(0,2)
                        setOrRefreshSpnLote()
                        Log.d("setOrRefreshSpnLote", "run? 1")
                    }
                    saldoProduto()
                }
            spnArmzOrig.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus == false){
                    if (!cursorArray.contains(spnArmzOrig.text.toString())) {
                        spnArmzOrig.setText("")
                        setOrRefreshSpnArmzOrig()
                    }
                }
            }
        }


        fun setOrRefreshSpnPrd() {


            var cursorPrd = db.getPrd()
            var cursorArray = ArrayList<Any>()
            if (cursorPrd != null) {
                cursorPrd.moveToFirst()
                cursorArray.add(cursorPrd.getString(2) + " - " + cursorPrd.getString(3))
                while (cursorPrd.moveToNext()) {
                    cursorArray.add(
                        cursorPrd.getString(2) + " - " + cursorPrd.getString(3)
                    )
                }
            }
            var simpleCursorAdapter =
                ArrayAdapter<Any>(this, android.R.layout.simple_dropdown_item_1line, cursorArray)
            spnPrd.setAdapter(simpleCursorAdapter) //spinner recebe os dados para exibição

            //ao selecionar um item
            spnPrd.onItemClickListener =
                AdapterView.OnItemClickListener { p0, view, position, _id ->
                    if (view?.context != null) {

                        spinnerCodPrd.text = spnPrd.text.substring(0,15)
                        saldoProduto()

                        //if (spinnerCodArmzOrig.length() > 0 && spinnerCodPrd.length() > 0) {
                            setOrRefreshSpnLote()
                            Log.d("setOrRefreshSpnLote", "run? 1")
                        //}
                    }
                }
        }


        fun setOrRefreshSpnArmzDest() {
            var cursorArmzDest = db.getArmz()
            var cursorArray = ArrayList<Any>()
            if (cursorArmzDest != null) {
                cursorArmzDest.moveToFirst()
                cursorArray.add(cursorArmzDest.getString(1))
                while (cursorArmzDest.moveToNext()) {
                    cursorArray.add(
                        cursorArmzDest.getInt(0),
                        cursorArmzDest.getString(1) + " - " + cursorArmzDest.getString(2)
                    )
                }
            }
            var simpleCursorAdapter =
                ArrayAdapter<Any>(this, android.R.layout.simple_dropdown_item_1line, cursorArray)
            spnArmzDest.setAdapter(simpleCursorAdapter) //spinner recebe os dados para exibição

            //ao selecionar um item
            spnArmzDest.onItemClickListener =
                AdapterView.OnItemClickListener { p0, view, position, _id ->
                    if (view?.context != null) {
                        spinnerCodArmzOrig.text = spnArmzOrig.text.substring(0,2)
                        if (spinnerCodArmzDest.length() > 0 && spinnerCodPrd.length() > 0) {
                            Log.d("setOrRefreshSpnLote", "run? 1")
                        }
                        saldoProduto()

                    }
                }
        }

        setOrRefreshSpnArmzOrig()
        setOrRefreshSpnPrd()
        setOrRefreshSpnArmzDest()

        fun salvar(){
            var query = ""
            var message = ""
            rastro = db.getRastro(spinnerCodPrd.text.toString())

            if (spinnerCodArmzOrig.length() == 0){
                message += "Armazem origem, "
            }
            if (spinnerCodPrd.length() == 0){
                message += "\nproduto, "
            }
            if (rastro == true) {
                if (spinnerCodLote.length() == 0){
                    message += "\nlote, "
                }
            } else

            if (spinnerCodPrd.length() == 0){
                message += "\nproduto, "
            }
            if (spinnerCodPrd.length() == 0){
                message += "\nproduto, "
            }




            var armzOrig = spinnerCodArmzOrig.text
            var prod = spinnerCodPrd.text
            var lote = spinnerCodLote.text
            var qtdMovimento = parseFloat(movimento.text.toString())
            var armzDest = spinnerCodArmzDest.text





            query = "INSERT INTO Movimento (armazemOrigem, codProduto, lote, qtdMovimento, armazemDestino, username, statusSync) " +
                                    "VALUES ('$armzOrig', '$prod', '$lote', ${qtdMovimento}, '$armzDest', '$username', 0)"
            db.externalExecSQL(query)
            //Connection().sendMovimento()

        }


        var finalizarBtn = findViewById<Button>(R.id.finalizar)
        finalizarBtn.setOnClickListener {
            salvar()
            finish()
        }
        var salvarBtn = findViewById<Button>(R.id.finalizar)
        salvarBtn.setOnClickListener {
            salvar()
            //limpar campos aqui
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
