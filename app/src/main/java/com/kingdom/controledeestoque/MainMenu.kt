package com.kingdom.controledeestoque

//
//import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.provider.AlarmClock
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kingdom.controledeestoque.database.Sync
import kotlinx.coroutines.*


class MainMenu(username: String?) : Fragment()/*, LifecycleEventObserver*/ {
    var username = username
    //lateinit var user: String
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    = inflater.inflate(R.layout.activity_main_menu, container, false).apply {
        var ctxt = getActivity()?.getApplicationContext()

        var relatorio = findViewById<FloatingActionButton>(R.id.relatorio)
        relatorio.setOnClickListener {
            var estatistica = Intent(ctxt, Relatorio::class.java)
            startActivity(estatistica)
        }

        var coordinator = findViewById<ConstraintLayout>(R.id.parent)
        var cl = findViewById<ConstraintLayout>(R.id.CL)

        var progressTB =
            findViewById<com.google.android.material.progressindicator.LinearProgressIndicator>(R.id.progressToolbar)
        val buttonTrArmz: Button = findViewById(R.id.trArmz)
        var syncBtn = findViewById<ExtendedFloatingActionButton>(R.id.syncBtn)

        fun showProgress(result: String) {
            if (result == "true") {
                progressTB.setVisibility(View.VISIBLE)
                syncBtn.setEnabled(false)
                syncBtn.setText("Sincronizando")
                buttonTrArmz.setEnabled(false)
                buttonTrArmz.text = "Aguarde a sincronização ser concluída"
            } else if (result == "false") {
                progressTB.setVisibility(View.INVISIBLE)
                syncBtn.setEnabled(true)
                syncBtn.setText("Sincronizar")
                buttonTrArmz.setEnabled(true)
                buttonTrArmz.setText("Transferência de armazem")
            } else if (result == "syncFail") {
                progressTB.setVisibility(View.INVISIBLE)
                syncBtn.setEnabled(true)
                syncBtn.setText("Falha ao sincronizar")
                buttonTrArmz.setEnabled(true)
                buttonTrArmz.setText("Falha ao sincronizar\nTransferência de armazem")

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
                    } else if (result == "Sucesso") {
                        Snackbar.make(
                            findViewById(R.id.CL),
                            "Sincronizado com sucesso!",
                            Snackbar.LENGTH_SHORT
                        ).setBackgroundTint(Color.parseColor("#197419"))
                            .setTextColor(Color.WHITE)
                            .setActionTextColor(
                                Color.WHITE
                            ).setAction("OK") {}.show()
                    }
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
            postConnectionView(rs)


            //Log.d("ConnectionView rtn", rtn)


            //postConnectionView(rtn)


        }

        CoroutineScope(Dispatchers.IO).async {
            connectionView()
        }


        //var sync = parseInt("")


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

        fun updateBadge(){ //Esse metodo atualiza o contador na barra de navegação
            var cursor = username?.let { SQLiteHelper(ctxt).countNotf(it) } //Conta quantas notificações não lidas existem para o usuario atual
            if (cursor != null) { //Por segurança, se o resultado for nulo (muito dificil) ele não fará nada.

                val activity: Activity? = activity //Instancia a atividade
                if (activity is Main_nav) { //confirma se a atividade foi instanciada
                    val myactivity: Main_nav? = activity as Main_nav?
                    myactivity?.getCount(cursor) //executa o metodo que atualiza o contador, passando o numero obtido pelo cursor na primeira linha da função.
                }
            }
        }

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
                            updateBadge()
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

        syncBtn.setOnClickListener {
            MainScope().launch {
                synchronization()
                /*if (rtn == "Sucesso"){
                    postSyncSuccess()
                }*/
            }


        }



        //val username = Intent().getStringExtra(AlarmClock.EXTRA_MESSAGE)
        val username = username
        var saudacao = "Bem-vindo, ${username}" // 11
        findViewById<TextView>(R.id.saudacao).apply { text = saudacao }

        var intent: Intent



        buttonTrArmz.setOnClickListener {
            intent = Intent(ctxt, TransferenciaDeArmazem::class.java)
                .apply {
                    putExtra(AlarmClock.EXTRA_MESSAGE, username)
                }
            startActivity(intent)
        }


        val buttonAP: Button = findViewById(R.id.apPerdas)
        buttonAP.setOnClickListener {
            /*intent = Intent(this, ApontamentoPerdas::class.java)
                .apply {
                    putExtra(AlarmClock.EXTRA_MESSAGE, username)}
            startActivity(intent)*/
        }


    }




    }






class FloatingActionButtonBehavior(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<FloatingActionButton?>() {
    fun layoutDependsOn(
        parent: CoordinatorLayout?,
        child: FloatingActionButton?,
        dependency: View?
    ): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    fun onDependentViewChanged(
        parent: CoordinatorLayout?,
        child: FloatingActionButton,
        dependency: View
    ): Boolean {
        val translationY =
            Math.min( dependency.getTranslationY().toFloat(), dependency.getHeight().toFloat())
        child.translationY = translationY
        return true
    }
}