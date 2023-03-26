package com.kingdom.controledeestoque

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.AlarmClock
import android.provider.Settings
import android.view.*
import android.widget.*
import android.support.*
import android.util.Log
import android.view.View.*
import kotlin.system.*
import kotlinx.coroutines.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.kingdom.controledeestoque.SQLiteHelper
import com.kingdom.controledeestoque.database.LoadContent
import com.kingdom.controledeestoque.database.Sync
import com.kingdom.controledeestoque.database.confirmUnPw

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var progress = findViewById<LinearProgressIndicator>(R.id.progressToolbar)


        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }




        /*val toolbar = findViewById<Toolbar?>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowCustomEnabled(false)
        }*/




        setContentView(R.layout.activity_main)
        SQLiteHelper(this);
        var db = SQLiteHelper(this)

        var sync = Sync()


        val user = findViewById<EditText>(R.id.editTextUsername)
        val userView = findViewById<TextInputLayout>(R.id.viewUser)

        val pw = findViewById<EditText>(R.id.editTextPassword)
        val pwView = findViewById<TextInputLayout>(R.id.viewPassword)

        fun syncIsDone(){
            var mainMenu = Intent(this, MainMenu::class.java).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, user.text.toString())
            }
            startActivity(mainMenu)

            finish()
        }


        //Exibir número de versão + revisão
        var versionCode = BuildConfig.VERSION_CODE
        var versionName = BuildConfig.VERSION_NAME
        var version = "Versão: $versionName. Revisão: $versionCode"

        var query: String

        //val user = String
        //val pw = String

        var elementsOnLogin = findViewById<ConstraintLayout>(R.id.elementsOnLogin)





        fun showProgress(result: String) {
            var progress = findViewById<LinearProgressIndicator>(R.id.progressToolbar)
            if (result == "true") {
                progress.setVisibility(VISIBLE)
            } else if (result == "false") {
                progress.setVisibility(GONE)
            }
        }




        fun connectionView(): String {

            var result = Sync().testConnection()

            if (result == "Falha") {
                var xyz = Snackbar.make(
                    findViewById(R.id.clMA),
                    getString(R.string.connectionErroResult1),
                    Snackbar.LENGTH_LONG
                ).setBackgroundTint(Color.parseColor("#741919"))
                    .setTextColor(Color.WHITE)
                    .setActionTextColor(Color.WHITE)
                    .setAction("OK") {}.show()
                return result
            } else if (result == "Sem Conexão") {
                return result
            } else {
                return "Sucesso"
            }
        }

        fun authUser(ctxt: android.content.Context) {
            showProgress("true")

            val user = findViewById<EditText>(R.id.editTextUsername)
            val userView = findViewById<TextInputLayout>(R.id.viewUser)

            val pw = findViewById<EditText>(R.id.editTextPassword)
            val pwView = findViewById<TextInputLayout>(R.id.viewPassword)

            var query: String
            val button: Button = findViewById(R.id.loginscreen_login)

            var db = SQLiteHelper(this)

            var sync = Sync()

            var elementsOnLogin = findViewById<ConstraintLayout>(R.id.elementsOnLogin)

            var result = connectionView()
            if (result == "Sucesso") {
                var validation = confirmUnPw(user.text.toString(), pw.text.toString())
                if (validation == 201) {
                    user.isEnabled = false
                    pw.isEnabled = false
                    button.isEnabled = false


                    Snackbar.make(
                        elementsOnLogin,
                        "Usuário autenticado. Aguarde.",
                        Snackbar.LENGTH_LONG
                    ).show()
                    query = "DELETE FROM Usuario WHERE username = '${user.text}'"
                    db.externalExecSQL(query)
                    query =
                        "INSERT INTO Usuario(username, password) VALUES ('${user.text}', '${pw.text}')"
                    db.externalExecSQL(query)

                    var username = user.text.toString()
                    //var progress = findViewById<LinearProgressIndicator>(R.id.progressToolbar)
                    //progress.visibility = VISIBLE

                    GlobalScope.launch {
                        try {
                            Looper.prepare()
                            sync.syncNoReturn(0, ctxt)
                        } catch (e: Exception){}
                        syncIsDone()

                        finish()
                    }
                } else if (validation == 401) {

                    userView.setError(" ")
                    pwView.setError("Nome de usuário ou senha incorretos")
                    pw.setText("")
                    Snackbar.make(
                        elementsOnLogin,
                        "Nome de usuário e/ou senha incorretos",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            if (result == "Falha" || result == "Sem Conexão") {
                query =
                    "SELECT username FROM Usuario WHERE username = '${user.text}' AND password = '${pw.text}'"
                var auth = db.externalExecSQLSelect(user.text.toString(), pw.text.toString())
                //Log.d("Debug", "Cursor = $cursor")
                if (auth == true) {
                    var username = user.text.toString()
                    var mainMenu = Intent(this, MainMenu::class.java).apply {
                        putExtra(AlarmClock.EXTRA_MESSAGE, username)
                    }
                    startActivity(mainMenu)
                    finish()
                } else if (auth == false) {
                    Snackbar.make(
                        elementsOnLogin,
                        "Não foi possivel conectar ao servidor. Verifique as configurações de rede e tente novamente.",
                        Snackbar.LENGTH_LONG
                    ).setAction("Abrir Configurações") {
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }.show()
                }
            }
        }
        val button: Button = findViewById(R.id.loginscreen_login)
        button.setOnClickListener {
            showProgress("true")
            authUser(this)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_actv, menu)
        return true
    }

    var versionCode = BuildConfig.VERSION_CODE
    var versionName = BuildConfig.VERSION_NAME


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {

        R.id.versionView -> {
            // User chose the "Settings" item, show the app settings UI...

            Toast.makeText(
                applicationContext,
                "Versão: $versionName", Toast.LENGTH_SHORT
            ).show()
            true
        }

        R.id.closeApp -> {
            finish()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }



}