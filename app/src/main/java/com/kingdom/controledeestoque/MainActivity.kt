package com.kingdom.controledeestoque

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.AlarmClock
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kingdom.controledeestoque.database.Sync
import com.kingdom.controledeestoque.database.confirmUnPw
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import java.lang.Integer.parseInt
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var prefs: PreferencesHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        prefs = PreferencesHelper(this)

        //var progress = findViewById<LinearProgressIndicator>(R.id.progressToolbar)
        var ctxt = this

        //SQLiteHelper(this);
        var db = SQLiteHelper(this)

        var sync = Sync()

        val user = findViewById<EditText>(R.id.usernameText)
        val userView = findViewById<TextInputLayout>(R.id.username)

        val pw = findViewById<EditText>(R.id.passwordText)
        val pwView = findViewById<TextInputLayout>(R.id.password)

        fun syncIsDone(){
            val username = user.text.toString()

            prefs.saveData("username", username)
            prefs.saveBoolean("isLoggedIn", true)

            var mainMenu = Intent(this, MainMenu::class.java).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, username)
            }
            startActivity(mainMenu)
            finish()
        }

        var ajudaBtn = findViewById<Button>(R.id.ajudaBtn)

        ajudaBtn.setOnClickListener{
            var intent = Intent(ctxt, PdfActivity::class.java)
            startActivity(intent)

            /*db.externalExecSQL("DELETE FROM Notificacao WHERE idNotificacao > 699")
            Log.d("Debug", "deletou")*/
        }


        fun showProgress(result: String) {
            var progress = findViewById<LinearProgressIndicator>(R.id.syncStatusIndicator)
            var userAuthStatus = findViewById<TextView>(R.id.userAuthStatusText)
            var syncStatus = findViewById<TextView>(R.id.syncStatusText)
            if (result == "Verificando") {
                progress.visibility = VISIBLE
                userAuthStatus.visibility = GONE
                syncStatus.visibility = VISIBLE
                syncStatus.text = "Verificando login e senha"
            } else if (result == "Erro") {
                progress.visibility = GONE
                userAuthStatus.visibility = GONE
                syncStatus.visibility = GONE
                syncStatus.text = "Atualizando base de dados"
            } else if (result == "Autenticado") {
                progress.visibility = VISIBLE
                userAuthStatus.visibility = VISIBLE
                syncStatus.visibility = VISIBLE
                syncStatus.text = "Atualizando base de dados"
            } else if (result == "Não autenticado") {
                progress.visibility = GONE
                userAuthStatus.visibility = GONE
                syncStatus.visibility = GONE
                syncStatus.text = "Atualizando base de dados"
            } else if (result == "Codigo 202") {
                progress.visibility = GONE
                userAuthStatus.visibility = GONE
                syncStatus.visibility = VISIBLE
                syncStatus.text = "Erro 202. Pode ser necessário atualizar sua senha."
            }
        }

        suspend fun connectionView(): String {
            var result = Sync().testConnection()

            if (result.toString() == "Falha") {
                lifecycleScope.launch(Dispatchers.Main) {
                    var xyz = Snackbar.make(
                        findViewById(R.id.main),
                        "Erro",
                        Snackbar.LENGTH_LONG
                    ).setBackgroundTint(Color.parseColor("#741919"))
                        .setTextColor(Color.WHITE)
                        .setActionTextColor(Color.WHITE)
                        .setAction("OK") {}.show()
                }
                return result.toString()

            } else if (result.toString() == "Sem Conexão") {
                return result.toString()
            } else {
                return "Sucesso"
            }
        }

        suspend fun runSync(): String {
            //CoroutineScope(CoroutineName("SyncMainActivity")).async(Dispatchers.Unconfined) {
            return withContext(Dispatchers.IO) {
                var rtn: String
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare()
                    }
                    rtn = sync.sync(0, ctxt).toString()
                    return@withContext rtn
                } catch (e: Exception) {
                    Log.d("SyncMainActivity (Thread)", e.toString())
                }
                //syncIsDone()
            } as String
        }
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView.rootView) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            if(imeVisible){
                ajudaBtn.isVisible = false
            } else {
                ajudaBtn.isVisible = true
            }
            insets
        }

        suspend fun authUser(ctxt: android.content.Context) {
            showProgress("Verificando")

            val user = findViewById<EditText>(R.id.usernameText)
            val userView = findViewById<TextInputLayout>(R.id.username)

            val pw = findViewById<EditText>(R.id.passwordText)
            val pwView = findViewById<TextInputLayout>(R.id.password)

            var query: String
            val button = findViewById<Button>(R.id.loginBtn)

            user.isEnabled = false
            pw.isEnabled = false
            button.isEnabled = false

            var db = SQLiteHelper(this)

            var elementsOnLogin = findViewById<ConstraintLayout>(R.id.main)

            var result = connectionView()

            if (result == "Sucesso") {
                var validation = withContext(Dispatchers.IO) {
                     confirmUnPw(user.text.toString(), pw.text.toString())
                }
                if (validation == 201) {
                    userView.error = ""
                    pwView.error = ""

                    showProgress("Autenticado")
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

                    prefs.saveData("username", username)
                    prefs.saveBoolean("isLoggedIn", true)
                    prefs.saveBoolean("isConnected", true)

                    var message = Sync().sync(0,this)

                    if (message == "Sucesso"){
                        var mainMenu = Intent(this, MainMenu::class.java)
                        startActivity(mainMenu)
                        finish()
                    } else {
                        showProgress("false")
                    }
                } else if (validation == 401) {
                    user.isEnabled = true
                    pw.isEnabled = true
                    button.isEnabled = true
                    showProgress("Não autenticado")
                    userView.error = " "
                    pwView.error = "Nome de usuário ou senha incorretos"
                    pw.setText("")
                    Snackbar.make(
                        elementsOnLogin,
                        "Nome de usuário e/ou senha incorretos",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else if (validation == 202) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Mensagem do Protheus")
                        .setMessage("""
                            Você precisa trocar sua senha. Use um computador próximo, abra o aplicativo Protheus e altere-a. 
                            Depois, clique em "Fechar" e tente novamente. 
                            Se isso não funcionar, contate o TI. 
                            Código de erro: 202
                        """.trimIndent() )
                        .setNegativeButton(
                            "Fechar"
                        ) { dialog, which ->
                        }.show()

                    user.isEnabled = true
                    pw.isEnabled = true
                    button.isEnabled = true
                    showProgress("Codigo 202")
                    userView.error = " "
                    pwView.error = "Nome de usuário ou senha incorretos"
                    pw.setText("")

                    showProgress("false")
                } else if (validation == 299) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Mensagem do Protheus")
                        .setMessage("Ocorreu um erro ao entrar com seu usuário e senha. Tente novamente, e se não funcionar, entre em contato com o TI.")
                        .setNegativeButton(
                            "Fechar"
                        ) { dialog, which ->

                        }.show()
                    showProgress("false")
                }
            }
            if (result == "Falha" || result == "Sem Conexão") {
                query =
                    "SELECT username FROM Usuario WHERE username = '${user.text}' AND password = '${pw.text}'"
                var auth = db.externalExecSQLSelect(user.text.toString(), pw.text.toString())
                if (auth == true) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Conexão não encontrada")
                        .setMessage("Não foi possivel conectar ao servidor, mesmo assim é possível trabalhar offline. \nVerifique as configurações de rede assim que possível.")
                        .setNegativeButton(
                            "Continuar"
                        ) { dialog, which ->
                            val username = user.text.toString()
                            prefs.saveData("username", username)
                            prefs.saveBoolean("isLoggedIn", true)
                            prefs.saveBoolean("isConnected", false)

                            var mainMenu = Intent(this, MainMenu::class.java)
                            startActivity(mainMenu)
                            finish()
                        }
                        .setNeutralButton("Abrir Configurações de Wi-fi") { dialog, which ->

                            val username = user.text.toString()
                            prefs.saveData("username", username)
                            prefs.saveBoolean("isLoggedIn", true)
                            prefs.saveBoolean("isConnected", false)

                            var mainMenu = Intent(this, MainMenu::class.java)
                            startActivity(mainMenu)
                            finish()
                            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))

                        }.show()
                    showProgress("false")

                } else if (auth == false) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Não foi possivel sincronizar")
                        .setMessage("Não foi possivel conectar ao servidor. \nVerifique as configurações de rede e tente novamente.")
                        .setNegativeButton(
                            "Fechar") { dialog, which ->
                            dialog.dismiss()
                        }
                        .setNeutralButton("Abrir Configurações de Wi-fi") { dialog, which ->
                            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                        }.show()
                    showProgress("Erro")
                    user.isEnabled = true
                    pw.isEnabled = true
                    button.isEnabled = true
                }
            }
        }

        val button: Button = findViewById(R.id.loginBtn)
        button.setOnClickListener {
            showProgress("true")
            var context = this
            MainScope().launch { authUser(context) }

        }
        var dateTime = "05/04/2023"
        var dty = dateTime.split("/").toTypedArray()
        var minute = "05:50"
        var dmi = minute.split(":").toTypedArray()

        var calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, parseInt(dty[0]))
        calendar.set(Calendar.MONTH, parseInt(dty[1]))
        calendar.set(Calendar.YEAR, parseInt(dty[2]))
        calendar.set(Calendar.HOUR_OF_DAY, parseInt(dmi[0]))
        calendar.set(Calendar.MINUTE, parseInt(dmi[1]))

        var date = "01/01/2023"//.toLong()


        val myEditText = findViewById<TextInputEditText>(R.id.passwordText)

        doSomething(myEditText)
    }

    private fun doSomething(doneClick: TextInputEditText){
        val button = findViewById<Button>(R.id.loginBtn)

        doneClick.setOnEditorActionListener(TextView.OnEditorActionListener{ _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {

                button.performClick()

                // Do something of your interest.
                // We in this examples created the following Toasts
                /*if(search.text.toString() == "geeksforgeeks"){
                    Toast.makeText(applicationContext, "Welcome to GFG", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Invalid Input", Toast.LENGTH_SHORT).show()
                }

                return@OnEditorActionListener true*/
            }
            false
        })
    }

    private fun setBarsAppearance() {
        // Alterar a cor das barras (status e navegação)
        window.statusBarColor = getColor(R.color.colorPrimary) // Cor da barra de status
        window.navigationBarColor = getColor(R.color.colorPrimary) // Cor da barra de navegação

        // Configurar ícones (claro ou escuro) com WindowInsetsControllerCompat
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        // Verifica tema atual e configura os ícones
        val isLightTheme = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_NO
        controller.isAppearanceLightStatusBars = isLightTheme
        controller.isAppearanceLightNavigationBars = isLightTheme
    }


    override fun onDestroy() {
        super.onDestroy()
        SQLiteHelper(this).close()
    }

    override fun onPause() {
        super.onPause()
        SQLiteHelper(this).close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_actv, menu)
        return true
    }
}