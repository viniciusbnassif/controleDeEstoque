package com.kingdom.controledeestoque

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.AlarmClock
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kingdom.controledeestoque.database.Sync
import kotlinx.coroutines.*
import java.lang.Float.parseFloat
import java.util.*
import kotlin.collections.ArrayList

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

        var bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView.rootView) { _, insets ->

            //This lambda block will be called, every time keyboard is opened or closed


            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            if(imeVisible){
                bottomAppBar.isVisible = false
            } else {
                bottomAppBar.isVisible = true
            }

            insets
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



        val dateFormatter = SimpleDateFormat("yyyyMMddkk:mm")
        val dty = dateFormatter.format(Date())



        var spinnerCodArmzOrig = findViewById<TextView>(R.id.spinnerCodArmzOrig)
        var spnArmzOrig = findViewById<AutoCompleteTextView>(R.id.spinnerArmzOrigem)


        var viewSpinnerPrd = findViewById<TextInputLayout>(R.id.viewSpinner)

        var spinnerCodPrd = findViewById<TextView>(R.id.spinnerCodPrd)
        var spnPrd = findViewById<AutoCompleteTextView>(R.id.spinnerPrd)

        var spinnerCodLote = findViewById<TextView>(R.id.spinnerCodLote)
        var saldoLote = findViewById<TextView>(R.id.saldoLote)
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
            var cursorArray = ArrayList<Any>()
            //var cursorPrd = db.getCodPrd(idPrd as String)

            if (codArmzOrig.length > 0 && codPrd.length > 0) {
                var cursorLote = db.getLote("$codArmzOrig", "$codPrd")


                if (cursorLote != null) {
                    cursorLote.moveToFirst()
                    cursorArray.add(cursorLote.getString(1) + " - Saldo: " + "${cursorLote.getFloat(2)}")
                    while (cursorLote.moveToNext()) {
                        cursorArray.add(cursorLote.getString(1) + " - Saldo: " + "${cursorLote.getFloat(2)}")
                    }
                    viewSpnLote.error = getString(R.string.campo_obrigatorio)
                    viewSpnLote.isEnabled = true
                } else {
                    viewSpnLote.error = ""
                    viewSpnLote.isErrorEnabled = false
                    viewSpnLote.isEnabled = false
                }
                var simpleCursorAdapter =
                    ArrayAdapter<Any>(this, android.R.layout.simple_dropdown_item_1line, cursorArray)
                spnLote.setAdapter(simpleCursorAdapter) //spinner recebe os dados para exibição
            }

            rastro = db.getRastro("$codPrd")
            if (rastro == true) {
                viewSpnLote.error = getString(R.string.campo_obrigatorio)

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
                    saldoLote.text = spnLote.text.substring(20)
                    saldoProduto()
                    spnLote.clearFocus()
                }
            spnLote.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus == false){
                    if (!cursorArray.contains(spnLote.text.toString())) {
                        spnLote.setText("")
                        spinnerCodLote.setText("")
                        setOrRefreshSpnLote()
                    }
                }
            }
        }

        fun setOrRefreshSpnArmzOrig() {

            var cursorArmzOrig = db.getArmz()
            var cursorArray = ArrayList<Any>()
            if (cursorArmzOrig != null) {
                cursorArmzOrig.moveToFirst()
                cursorArray.add("${cursorArmzOrig.getString(1)} - ${cursorArmzOrig.getString(2)}")
                while (cursorArmzOrig.moveToNext()) {
                    cursorArray.add(
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
                    spnArmzOrig.clearFocus()
                    saldoProduto()
                }
            spnArmzOrig.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus == false){
                    if (!cursorArray.contains(spnArmzOrig.text.toString())) {
                        spnArmzOrig.setText("")
                        spinnerCodArmzOrig.setText("")
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
                    spnPrd.clearFocus()
                }
            spnPrd.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus == false){
                    if (!cursorArray.contains(spnPrd.text.toString())) {
                        spnPrd.setText("")
                        spinnerCodPrd.setText("")
                        setOrRefreshSpnPrd()
                    }
                }
            }
        }


        fun setOrRefreshSpnArmzDest() {
            var cursorArmzDest = db.getArmz()
            var cursorArray = ArrayList<Any>()
            if (cursorArmzDest != null) {
                cursorArmzDest.moveToFirst()
                cursorArray.add(cursorArmzDest.getString(1) + " - " + cursorArmzDest.getString(2))
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
                        spinnerCodArmzDest.text = spnArmzDest.text.substring(0,2)
                        if (spinnerCodArmzDest.length() > 0 && spinnerCodPrd.length() > 0) {
                            Log.d("setOrRefreshSpnLote", "run? 1")
                        }
                        saldoProduto()
                    }
                    spnArmzDest.clearFocus()
                }
            spnArmzDest.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus == false){
                    if (!cursorArray.contains(spnArmzDest.text.toString())) {
                        spnArmzDest.setText("")
                        spinnerCodArmzDest.setText("")
                        setOrRefreshSpnArmzDest()
                    }
                }
            }
        }

        setOrRefreshSpnArmzOrig()
        setOrRefreshSpnPrd()
        setOrRefreshSpnArmzDest()

        fun salvar(cod: Int): AlertDialog? {
            var query = ""
            var message = ""
            var rastro = db.getRastro(spinnerCodPrd?.text.toString())


            var armzOrig = spinnerCodArmzOrig.text
            var prod = spinnerCodPrd.text
            var lote = spinnerCodLote.text
            var qtdMovimento = if(!movimento.text.isNullOrEmpty()){parseFloat(movimento.text.toString())} else {0}
            var armzDest = spinnerCodArmzDest.text


            if (spinnerCodArmzOrig.text.isEmpty()){
                message += "- Armazem origem; "
            }
            if (spinnerCodPrd.text.isEmpty()){
                message += "\n- Produto; "
            }
            if (rastro && spinnerCodLote.text.isEmpty()) {
                message += "\n- Lote; "
            }

            if (movimento.text?.isEmpty() == true){
                message += "\n- Quantidade a movimentar; "
            } else {
                if (spinnerCodLote.text.isEmpty() == false){
                    var movimentoresult = parseFloat(movimento.text.toString())
                    var saldoLoteResult = parseFloat(saldoLote.text.toString())
                    if (movimentoresult > saldoLoteResult){
                        movimento.setText("")
                        var showMessage = MaterialAlertDialogBuilder(this)
                            .setTitle("Você está tentando movimentar uma quantidade maior que existe no lote.")
                            .setMessage("Verifique o valor de saldo no campo lote.")
                            .setPositiveButton("Fechar") { dialog, which ->
                                dialog.dismiss()
                            }
                            .setCancelable(false)
                        return showMessage.show()
                    }
                }
            }
            if (spnArmzDest.text.isEmpty()){
                message += "\n- Armazem de destino; "
            }
            var showMessage = MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_baseline_deselect_24_black)
                .setTitle("Existem campos não preenchidos.")
                .setMessage("Verifique os campos a seguir: \n$message")
                .setPositiveButton("Fechar") { dialog, which ->
                    dialog.dismiss()
                }
                .setCancelable(false)
            if (message.length >0) {
                return showMessage.show()
            }
            else if(rastro != null || !armzOrig.isNullOrEmpty() || !prod.isNullOrEmpty() || !lote.isNullOrEmpty() || qtdMovimento != 0 || !armzDest.isNullOrEmpty() || !username.isNullOrEmpty()){
                query =
                    "INSERT INTO Movimento (armazemOrigem, codProduto, lote, qtdMovimento, armazemDestino, dataHora, username, statusSync) " +
                            "VALUES ('$armzOrig', '$prod', '$lote', ${qtdMovimento}, '$armzDest', '$dty', '$username', 0)"
                db.externalExecSQL(query)
                //Connection().sendMovimento()
                Log.d("Debug completed??", "true, apparently")
             }
            var ctxt = this
            CoroutineScope(CoroutineName("syncMovimento")).launch(Dispatchers.Unconfined) {
                try {
                    Log.d("Seek then out", "true, apparently")
                    if (Looper.myLooper() == null) {
                        Looper.prepare()
                    }
                    Sync().sync(1, ctxt)
                } catch (e: Exception){}
                cancel()
            }
            if (cod == 1){
                startActivity(getIntent())
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            } else if (cod == 0){
                finish()
                return null
            }
            return null

        }


        var finalizarBtn = findViewById<Button>(R.id.finalizar)
        finalizarBtn.setOnClickListener {
            salvar(0)
        }
        var salvarBtn = findViewById<Button>(R.id.salvar)
        salvarBtn.setOnClickListener {
            salvar(1)
            //limpar campos aqui
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
