package com.kingdom.controledeestoque

//
//import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.http.SslCertificate.saveState
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.AlarmClock
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kingdom.controledeestoque.database.Sync
import kotlinx.coroutines.*


class MainMenu : AppCompatActivity(), LifecycleEventObserver {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)



        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }


        var relatorio = findViewById<FloatingActionButton>(R.id.relatorio)
        relatorio.setOnClickListener {
            var estatistica = Intent(this, Relatorio::class.java)
            startActivity(estatistica)
        }

        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowCustomEnabled(false)

        }
        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.ic_baseline_logout_24)


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
                buttonTrArmz.setEnabled(false)
                buttonTrArmz.setText("Falha ao sincronizar")

                MaterialAlertDialogBuilder(this)
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
                        finish()
                    }

                    .setCancelable(false)
                    .show()

            }
        }




        fun postConnectionView(result: String) {
            MainScope().launch(CoroutineName("MainMenu")) {
                try {
                    if (result == "Falha" || result == "") {
                        val snackbar = Snackbar.make(
                            findViewById(R.id.CL),
                            "Não foi possível estabelecer uma conexão com o servidor",
                            Snackbar.LENGTH_INDEFINITE
                        ).setBackgroundTint(Color.parseColor("#741919")).setTextColor(Color.WHITE)
                            .setActionTextColor(
                                Color.WHITE
                            ).setAction("OK") {}.show()
                    } else if (result == "Sem Conexão") {
                        showProgress("false")
                        Snackbar.make(
                            findViewById(R.id.CL),
                            "Não foi possível estabelecer uma conexão com o servidor (Endereço e porta indisponíveis para esta rede)",
                            Snackbar.LENGTH_LONG
                        ).setBackgroundTint(Color.parseColor("#E3B30C")).setTextColor(Color.WHITE)
                            .setActionTextColor(
                                Color.WHITE
                            ).setAction("OK") {}.show()
                    } else if (result == "Sucesso") {
                        Snackbar.make(
                            findViewById(R.id.CL),
                            "Sincronizado com sucesso!",
                            Snackbar.LENGTH_SHORT
                        ).setBackgroundTint(Color.parseColor("#197419")).setTextColor(Color.WHITE)
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
            var result = ""

            //CoroutineScope(Dispatchers.IO).async(CoroutineName("testConnectionMM")) {
            CoroutineScope(CoroutineName("testConnectionMM")).async(Dispatchers.Unconfined) {
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare()
                    }
                    result = Sync().testConnection()

                    Log.d("ConnectionView result", result)
                    postConnectionView(result)

                } catch (e: Exception) {
                    Log.d("connectionView", e.toString())
                }

                Log.d("ConnectionView result2", result)
                //return result
                MainScope().launch {
                    postConnectionView(result)
                }
            }

            //Log.d("ConnectionView rtn", rtn)


            //postConnectionView(rtn)


        }

        CoroutineScope(Dispatchers.Unconfined).launch {
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
            MainScope().async {
                try {
                    showProgress("false")
                    Log.d("run?", "run?")
                } catch (e: Exception) {
                    Log.d("run e?", e.toString())
                }
            }
        }

        suspend fun synchronization() {

            var msg = ""
            var ctxt = this

            MainScope().async {
                try {
                    showProgress("true")
                    Log.d("run?", "run?")
                } catch (e: Exception) {
                    Log.d("run e?", e.toString())
                }
            }.await()


            //var global = LifecycleEventObserver()
            CoroutineScope(CoroutineName("SyncMainMenu")).async(Dispatchers.Unconfined) { // <------------------------------------------------

                var msgrtn = ""
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare()
                    }
                    var thisHandler = Handler()
                    msgrtn = Sync().sync(0, ctxt)
                } catch (e: Exception) {
                    Log.d("MainMenu", e.toString())
                    MainScope().async {
                        showProgress("syncFail")
                    }

                }
                if (msgrtn == "Sucesso") {
                    MainScope().run { postSyncSuccess() }

                } else if (msgrtn == "Falha") {
                    connectionView()
                    cancel()
                }
                //cancel()
            }
        }

        syncBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Unconfined) {
                try {
                    synchronization()
                } catch (e: Exception) {
                }
            }
        }


        val username = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)
        var saudacao = "Bem-vindo, ${username}"
        findViewById<TextView>(R.id.saudacao).apply { text = saudacao }

        var intent: Intent



        buttonTrArmz.setOnClickListener {
            intent = Intent(this, TransferenciaDeArmazem::class.java)
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
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }






    //var btnSync = findViewById<MaterialButton>(R.id.syncBtn)

    /*@SuppressLint("ResourceAsColor")
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {

        R.id.syncBtn -> {
            // User chose the "Settings" item, show the app settings UI...

            //btnSync.setBackgroundColor(R.color.black)
            sync.sync(0, this)
            //btnSync.setBackgroundColor(R.color.blue_700)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }*/


    /* When the activity is destroyed then close the cursor as it will not be used again */
    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_baseline_logout_24_black)
            .setTitle("Você escolheu sair...")
            .setMessage("O aplicativo será encerrado e será necessário efetuar login novamente. \nDeseja sair mesmo assim?")
            .setNeutralButton("Permanecer no menu") { dialog, which ->
                dialog.dismiss()
            }
            .setNegativeButton("Fazer logout") { dialog, which ->
                super.onBackPressed()
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            .setPositiveButton("Fechar app") { dialog, which ->
                finish()
            }
            .show()
    }

    /* When the activity is destroyed then close the cursor as it will not be used again */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



    val lifecycleEventObserver = LifecycleEventObserver { source, event ->
        if (event == Lifecycle.Event.ON_RESUME ) {
            Log.e( "APP" , "resumed" )
        }
        else if ( event == Lifecycle.Event.ON_PAUSE ) {
            Log.e( "APP" , "paused" )
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        MainScope().launch() {
            when(event){


                Lifecycle.Event.ON_STOP -> {
                }
                Lifecycle.Event.ON_CREATE -> Sync().testConnection()

                Lifecycle.Event.ON_START -> {
                }
                Lifecycle.Event.ON_RESUME -> {
                }
                Lifecycle.Event.ON_PAUSE -> {
                }
                Lifecycle.Event.ON_DESTROY -> finish()
                Lifecycle.Event.ON_ANY -> {
                }
            }
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