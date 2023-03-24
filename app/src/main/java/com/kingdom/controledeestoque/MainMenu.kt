package com.kingdom.controledeestoque

//import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kingdom.controledeestoque.database.Sync
import java.lang.Integer.parseInt

class MainMenu : AppCompatActivity() {
    //@SuppressLint("ResourceAsColor", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)


        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        var relatorio = findViewById<FloatingActionButton>(R.id.relatorio)
        relatorio.setOnClickListener{
            var estatistica = Intent(this, Relatorio::class.java)
            startActivity(estatistica)
        }

        val toolbar = findViewById<Toolbar>(R.id.appBar)
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

        fun connectionView(){
            var result = Sync().testConnection()

            if (result == "Falha" ) {
                val snackbar = Snackbar.make(findViewById(R.id.CL),
                    "Não foi possível estabelecer uma conexão com o servidor",
                    Snackbar.LENGTH_INDEFINITE
                ).setBackgroundTint(Color.parseColor("#741919")).setTextColor(Color.WHITE).setActionTextColor(
                    Color.WHITE).setAction("OK"){}.show()
            } else if (result == "Sem Conexão") {
                Snackbar.make(findViewById(R.id.CL),
                    "Não foi possível estabelecer uma conexão com o servidor (Endereço e porta indisponíveis para esta rede)",
                    Snackbar.LENGTH_INDEFINITE
                ).setBackgroundTint(Color.parseColor("#E3B30C")).setTextColor(Color.WHITE).setActionTextColor(
                    Color.WHITE).setAction("OK"){}.show()
            }else {
                Snackbar.make(findViewById(R.id.CL),
                    "Sincronizado com sucesso!",
                    Snackbar.LENGTH_LONG
                ).setBackgroundTint(Color.parseColor("#197419")).setTextColor(Color.WHITE).setActionTextColor(
                    Color.WHITE).setAction("OK"){}.show()
            }
        }

        connectionView()


        //var sync = parseInt("")



        var syncBtn = findViewById<ExtendedFloatingActionButton>(R.id.syncBtn)
        syncBtn.setOnClickListener {
            var sync = Sync().sync(0, this)
            if (sync == "Sucesso") {

                Snackbar.make(cl,
                    "Sincronizado com sucesso!",
                    Snackbar.LENGTH_INDEFINITE
                ).setBackgroundTint(Color.parseColor("#197419")).setTextColor(Color.WHITE).setActionTextColor(
                    Color.WHITE).setAction("OK"){}.show()
            } else if (sync == "Falha") {
                connectionView()
            }
        }

        val username = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)
        var saudacao = "Bem-vindo, ${username}"
        findViewById<TextView>(R.id.saudacao).apply { text = saudacao }

        var intent: Intent




        val buttonAE: Button = findViewById(R.id.apEmbalados)
        buttonAE.setOnClickListener {
            /*intent = Intent(this, ApontamentoEmbalados1::class.java)
                .apply {
                    putExtra(AlarmClock.EXTRA_MESSAGE, username)}
            startActivity(intent)*/
        }



        val buttonAP: Button = findViewById(R.id.apPerdas)
        buttonAP.setOnClickListener {
            /*intent = Intent(this, ApontamentoPerdas::class.java)
                .apply {
                    putExtra(AlarmClock.EXTRA_MESSAGE, username)}
            startActivity(intent)*/
        }
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