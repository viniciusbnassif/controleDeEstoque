package com.kingdom.controledeestoque

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.kingdom.controledeestoque.database.Sync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Boolean.TRUE

class MainMenu : AppCompatActivity() {
    //lateinit var username : String
    private lateinit var prefs: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)


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
        val username = prefs.getData("username", "Guest")

        //val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.appBar)
        //setSupportActionBar(toolbar)

        var ctxt = applicationContext

        var cl = findViewById<ConstraintLayout>(R.id.main)

        //var progressTB = findViewById<com.google.android.material.progressindicator.LinearProgressIndicator>(R.id.progressToolbar)
        val buttonTrArmz= findViewById<Button>(R.id.trArmz)
        val buttonRelat= findViewById<MaterialButton>(R.id.relatorio)
        val buttonNotif = findViewById<MaterialButton>(R.id.notificacoes)
        var syncBtn = findViewById<MaterialButton>(R.id.syncBtn)


        fun showProgress(result: String) {
            if (result == "true") {
                //progressTB.setVisibility(View.VISIBLE)
                syncBtn.setEnabled(false)
                syncBtn.setText("Sincronizando")
                buttonTrArmz.setEnabled(false)
                buttonTrArmz.text = "Aguarde a sincronização ser concluída"
                buttonRelat.setEnabled(false)
                buttonNotif.setEnabled(false)
            } else if (result == "false") {
                //progressTB.setVisibility(View.INVISIBLE)
                syncBtn.setEnabled(true)
                syncBtn.setText("Sincronizar")
                buttonTrArmz.setEnabled(true)
                buttonTrArmz.setText("Transferência de armazem")
                buttonRelat.setEnabled(true)
                buttonNotif.setEnabled(true)
            } else if (result == "syncFail") {
                //progressTB.setVisibility(View.INVISIBLE)
                syncBtn.setEnabled(true)
                syncBtn.setText("Falha ao sincronizar")
                buttonTrArmz.setEnabled(true)
                buttonTrArmz.setText("Falha ao sincronizar\nTransferência de armazem")
                buttonRelat.setEnabled(true)
                buttonNotif.setEnabled(true)

                // IMPLEMENTAR MUDANÇA: TELA DE NOTIFICAÇÕES DEVE FICAR BLOQUEADA ATÉ FINAL DA SINCRONIZAÇÃO

                if (ctxt != null) {
                    MaterialAlertDialogBuilder(ctxt)
                        .setIcon(R.drawable.ic_baseline_sync_problem_24)
                        .setTitle("Ocorreu um erro durante a sincronização")
                        .setMessage("A conexão foi interrompida durante a sincronização e suas alterações podem não ter sido salvas! \nVerifique as configurações ou reinicie o aplicativo e tente novamente")
                        .setPositiveButton("Tentar novamente") { dialog, which ->
                            try {
                                syncBtn.performClick()
                            } catch (e: Exception) {
                            }
                            dialog.dismiss()
                        }
                        .setNeutralButton("Abrir Configs.") { dialog, which ->
                            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                            showProgress("syncFail")
                        }
                        .setNegativeButton("Fechar aplicativo") { dialog, which ->
                            //View.finish()
                        }

                        .setCancelable(false)
                        .show()
                }
            }
        }

        val mainLayout = findViewById<View>(R.id.main_layout)
        val retryBTN = findViewById<MaterialButton>(R.id.refreshConnection)




        suspend fun postConnectionView(result: String) {
            return withContext(Dispatchers.Main) {
                try {
                    if (result == "Falha") {
                        /*val snackbar = Snackbar.make(
                        findViewById(R.id.CL),
                        "Não foi possível estabelecer uma conexão com o servidor",
                        Snackbar.LENGTH_INDEFINITE
                    ).setBackgroundTint(Color.parseColor("#741919")).setTextColor(Color.WHITE)
                        .setActionTextColor(
                            Color.WHITE
                        ).setAction("OK") {}.show()*/
                        if (ctxt != null) {
                            MaterialAlertDialogBuilder(ctxt)
                                .setTitle("Erro ao conectar")
                                .setMessage("Não foi possivel estabelecer uma conexão com o servidor. Tente novamente.")
                                .setNegativeButton("Fechar") { dialog, which ->
                                    dialog.dismiss()
                                }
                        }
                        mainLayout.visibility = View.VISIBLE
                    } else if (result == "Sem Conexão" || result == "") {
                        if (ctxt != null) {
                            MaterialAlertDialogBuilder(ctxt)
                                .setTitle("Erro ao conectar")
                                .setMessage(
                                    "Não foi possível estabelecer uma conexão com o servidor. \n\nEndereço e porta indisponíveis para esta rede\n" +
                                            "Isso geralmente acontece quando o tablet está conectado a uma rede incorreta."
                                )
                                .setNegativeButton("Fechar") { dialog, which ->
                                    dialog.dismiss()
                                }
                                .setNeutralButton("Abrir Configurações Wi-fi") { dialog, which ->
                                    dialog.dismiss()
                                }
                        }
                        /*showProgress("false")
                    Snackbar.make(
                        findViewById(R.id.CL),
                        "Não foi possível estabelecer uma conexão com o servidor (Endereço e porta indisponíveis para esta rede)",
                        Snackbar.LENGTH_LONG
                    ).setBackgroundTint(Color.parseColor("#E3B30C")).setTextColor(Color.WHITE)
                        .setActionTextColor(
                            Color.WHITE
                        ).setAction("OK") {}.show()*/
                        mainLayout.visibility = View.VISIBLE
                    } else if (result == "Sucesso") {
                        Snackbar.make(
                            findViewById(R.id.main),
                            "Sincronizado com sucesso!",
                            Snackbar.LENGTH_SHORT
                        ).setBackgroundTint(Color.parseColor("#197419"))
                            .setTextColor(Color.WHITE)
                            .setActionTextColor(
                                Color.WHITE
                            ).setAction("OK") {}.show()
                    }
                    mainLayout.visibility = View.GONE
                } catch (e: Exception) {
                    Log.d("postConnectionView", e.toString())
                }
                showProgress("false")
            }
        }


        suspend fun connectionView() {
            var result: String

            //CoroutineScope(Dispatchers.IO).async(CoroutineName("testConnectionMM")) {
                var rs = ""
                    rs = Sync().testConnection().toString()

            postConnectionView(rs)
            Log.d("ConnectionView result2", rs)
            //return result
        }

        CoroutineScope(Dispatchers.IO).async {
            connectionView()
        }

        retryBTN.setOnClickListener {
            mainLayout.visibility = View.GONE
            CoroutineScope(Dispatchers.IO).launch {
                connectionView()
            }
        }

        var accountBtn = findViewById<MaterialButton>(R.id.accountBtn)
        accountBtn.setOnClickListener {
            val modalBottomSheet = ModalBottomSheet(username)
            modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
        }

        fun postSyncSuccess() {
            Snackbar.make(
                cl,
                "Sincronizado com sucesso!",
                Snackbar.LENGTH_INDEFINITE
            ).setBackgroundTint(Color.parseColor("#197419")).setTextColor(Color.WHITE)
                .setActionTextColor(
                    Color.WHITE
                ).setAction("OK") {}.show()
            MainScope().launch {
                try {
                    showProgress("false")
                    Log.d("run?", "run?")
                } catch (e: Exception) {
                    Log.d("run e?", e.toString())
                }
            }
        }

        /*fun updateBadge(){ //Esse metodo atualiza o contador na barra de navegação
            var cursor = username?.let { SQLiteHelper(ctxt).countNotf(it) } //Conta quantas notificações não lidas existem para o usuario atual
            if (cursor != null) { //Por segurança, se o resultado for nulo (muito dificil) ele não fará nada.

                val activity: Activity? = activity //Instancia a atividade
                if (activity is Main_nav) { //confirma se a atividade foi instanciada
                    val myactivity: Main_nav? = activity as Main_nav?
                    myactivity?.getCount(cursor) //executa o metodo que atualiza o contador, passando o numero obtido pelo cursor na primeira linha da função.
                }
            }
        }*/

        suspend fun synchronization() {
            var msg = ""

            MainScope().async {
                try {
                    showProgress("true")
                    Log.d("run?", "run?")
                } catch (e: Exception) {
                    Log.d("run e?", e.toString())
                }
            }.await()

            CoroutineScope(Dispatchers.Unconfined).launch {
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare()
                    }
                    val r = ctxt?.let { Sync().sync(0, it).toString() }

                    if (r == "Sucesso"){
                        MainScope().launch {
                            //updateBadge()
                            try {
                                showProgress("false")
                                Log.d("run?", "run?")

                            } catch (e: Exception) {
                                Log.d("run e?", e.toString())

                            }
                        }
                    } else if (r == "Falha"){
                        MainScope().launch {
                            try {
                                showProgress("syncFail")
                                Log.d("run?", "run?")
                            } catch (e: Exception) {
                                Log.d("run e?", e.toString())
                            }
                        }
                    } else {

                    }
                } catch (e: Exception) {
                    Log.d("SyncMainActivity (Thread)", e.toString())
                    //return@launch "Falha"
                }

                //syncIsDone()
            }

        }

        var customAlertDialogView : View = layoutInflater.inflate(R.layout.alertdialog_sync_running, null)
        var materialAlertSync =
            MaterialAlertDialogBuilder(this)
                .setView(customAlertDialogView)
                .setTitle("Sincronizando")
                .setCancelable(false)

        customAlertDialogView.findViewById<LinearProgressIndicator>(R.id.progressBar).setActivated(TRUE)

        suspend fun doSync() {
            syncBtn.isEnabled = false
            var show = materialAlertSync.show()
            var thisctxt = this
            //var data = String
            // <- extension on current scope
            Log.d("context inside method from button sync", "${ctxt.toString()}")
            CoroutineScope(Dispatchers.IO).run {
                if (username != "Guest") {
                    var data = Sync().sync(0, applicationContext!!)
                    //val result = data.await()
                    if (data == "Sucesso") {
                        MainScope().launch {
                            show.cancel()
                            //whileSync(false)
                            /*Snackbar.make(
                    cl,
                    "Sincronizado com sucesso!",
                    Snackbar.LENGTH_SHORT
                ).setBackgroundTint(Color.parseColor("#197419")).setTextColor(Color.WHITE)
                    .setActionTextColor(Color.WHITE).setAction("OK") {}.show()*/
                            MaterialAlertDialogBuilder(thisctxt)
                                .setTitle("Sincronizando")
                                .setMessage("Sincronizado com sucesso")
                                .setCancelable(false)
                                //.setNeutralButton("Fechar") { dialog, _ -> (requireActivity() as MainNav).restartFragment(R.id.menu) }.show()
                                .setNeutralButton("Fechar") { dialog, _ ->
                                    var mainMenu = Intent(ctxt, MainMenu::class.java)
                                    startActivity(mainMenu)
                                    finish()
                                }
                                .show()
                        }
                    } else if (data == "Falha") {
                        MainScope().launch {
                            show.cancel()
                            MaterialAlertDialogBuilder(thisctxt)
                                .setTitle("Falha")
                                .setMessage("Ocorreu um erro ao sincronizar. Verifique o estado da conexão e tente novamente.")
                                .setCancelable(false)
                                //.setNeutralButton("Fechar") { dialog, _ -> (requireActivity() as MainNav).restartFragment(R.id.menu) }.show()
                                .setNeutralButton("Fechar") { dialog, _ ->
                                    var mainMenu = Intent(ctxt, MainMenu::class.java).apply {
                                        putExtra(EXTRA_MESSAGE, username)
                                    }
                                    startActivity(mainMenu)
                                    finish()
                                }
                                .show()
                        }
                    } else {
                        MainScope().launch {
                            MaterialAlertDialogBuilder(thisctxt)
                                .setTitle("Falha")
                                .setMessage("Ocorreu um erro ao sincronizar. Verifique o estado da conexão e tente novamente.")
                                .setCancelable(false)
                                //.setNeutralButton("Fechar") { dialog, _ -> (requireActivity() as MainNav).restartFragment(R.id.menu) }.show()
                                .setNeutralButton("Fechar") { dialog, _ ->
                                    var mainMenu = Intent(ctxt, MainMenu::class.java).apply {
                                        putExtra(EXTRA_MESSAGE, username)
                                    }
                                    startActivity(mainMenu)
                                    finish()
                                }
                                .show()

                            show.cancel()
                        }
                    }
                } else {
                    Snackbar.make(
                        cl,
                        "Error: Username = 'Guest'",
                        Snackbar.LENGTH_SHORT
                    ).setBackgroundTint(Color.parseColor("#000000")).setTextColor(Color.WHITE)
                        .setActionTextColor(Color.WHITE).setAction("OK") {}.show()
                }
            }
        }


        syncBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                //whileSync(true)
                doSync()
            }
        }
        var intent: Intent


        buttonTrArmz.setOnClickListener {
            intent = Intent(ctxt, TransferenciaDeArmazem::class.java)
            startActivity(intent)
        }
        buttonRelat.setOnClickListener {
            intent = Intent(ctxt, Relatorio::class.java)
            startActivity(intent)
        }
        buttonNotif.setOnClickListener {
            intent = Intent(ctxt, Notificacoes::class.java)
            startActivity(intent)
        }


        // Adicionar MenuProvider
        /*addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Infla o menu
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Lidar com cliques no menu
                return when (menuItem.itemId) {
                    R.id.accountView -> {
                        val modalBottomSheet = ModalBottomSheet(username)
                        modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)

                        false
                    }
                    else -> false
                }
            }
        }, this) // Menu ativo enquanto o lifecycle estiver em RESUMED*/

    }
}

class ModalBottomSheet(username: String?) : BottomSheetDialogFragment() {
    //var user = username
    private lateinit var prefs: PreferencesHelper

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.bottom_sheet, container, false).apply {
        prefs = activity?.let { PreferencesHelper(it.applicationContext) }!!

        var ctxt = activity?.applicationContext

        var userview = findViewById<TextView>(R.id.textView)
        var guia = findViewById<MaterialButton>(R.id.btnGuia)
        var sair = findViewById<MaterialButton>(R.id.btnSair)
        var versao = findViewById<MaterialButton>(R.id.versionView)
        userview.text = prefs.getData("username", "Guest")
        guia.setOnClickListener{
            //val contentUri = FileProvider.getUriForFile(context, "com.liderMinas.PCP", getResources().openRawResource(R.drawable.guia);)
            var intent = Intent(ctxt, PdfActivity::class.java)
            startActivity(intent)
        }
        sair.setOnClickListener {
            var intent = Intent(ctxt, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()
        }

        var pInfo: PackageInfo? = null
        try {
            pInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val versionName = pInfo!!.versionName //Version Name
        val versionCode = pInfo!!.versionCode //
        val verCode = pInfo.versionCode //Version Code

        versao.text = "Versão do aplicativo: ${versionName}"
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            behavior.setPeekHeight(resources.displayMetrics.heightPixels)
        }
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }
}
