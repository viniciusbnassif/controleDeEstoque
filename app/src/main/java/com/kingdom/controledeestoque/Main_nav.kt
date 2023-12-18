package com.kingdom.controledeestoque

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class Main_nav : AppCompatActivity() {


    lateinit var username : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.white));
            var view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        }



        //bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)

        username = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)!!
        //username = Intent().getStringExtra(AlarmClock.EXTRA_MESSAGE)!!


        val MainMenu=MainMenu(username)
        val Relatorio=Relatorio()
        val Notificacoes=Notificacoes(username, this)
        val Requisicao=Requisicao(username, this)


        setCurrentFragment(MainMenu)

        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            getCount(SQLiteHelper(this).countNotf(username))
            when(it.itemId){
                R.id.menu->setCurrentFragment(MainMenu)
                R.id.relatorioNav->setCurrentFragment(Relatorio)
                R.id.notificacoes->setCurrentFragment(Notificacoes)
                R.id.requisicoes->setCurrentFragment(Requisicao)
            }
            true
        }
        getCount(SQLiteHelper(this).countNotf(username))


        //updateBadge(this, username)
    }

    override fun onDestroy() {
        super.onDestroy()
        SQLiteHelper(this).close()
    }

    override fun onPause() {
        super.onPause()
        SQLiteHelper(this).close()
    }




    fun getCount(count: Int){
        updateBadge(count)
    }
    fun updateBadge(count: Int) {
        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        var badge = bottomNavigationView.getOrCreateBadge(R.id.notificacoes)
        if (count != 0) {
            badge.isVisible = true
            // An icon only badge will be displayed unless a number or text is set:
            badge.number = count
        } else {
            badge.isVisible = false
        }
    }



    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_activity_main_nav,fragment)
            commit()
        }
    public val mainNavContext = this
}