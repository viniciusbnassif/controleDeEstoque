package com.kingdom.controledeestoque

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView

class Main_nav : AppCompatActivity() {
    lateinit var username : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)

        /*if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.white));
            var view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        }*/
        //bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)

        username = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)!!

        //val MainMenu=MainMenu(username)
        val Relatorio=Relatorio()
        val Notificacoes=Notificacoes()
        val Requisicao=Requisicao(username, this)

        /*setCurrentFragment(
            MainMenu
        )*/


        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            getCount(SQLiteHelper(this).countNotf(username))
            when(it.itemId){
                /*R.id.menu->setCurrentFragment(MainMenu)
                R.id.relatorioNav->setCurrentFragment(Relatorio)
                R.id.notificacoes->setCurrentFragment(Notificacoes)
                R.id.requisicoes->setCurrentFragment(Requisicao)*/
            }
            true
        }

        var navRail = findViewById<NavigationRailView>(R.id.nav_viewRail)
        navRail.setOnItemSelectedListener {
            getCount(SQLiteHelper(this).countNotf(username))
            when(it.itemId){
                /*R.id.menu->setCurrentFragment(MainMenu)
                R.id.relatorioNav->setCurrentFragment(Relatorio)
                R.id.notificacoes->setCurrentFragment(Notificacoes)
                R.id.requisicoes->setCurrentFragment(Requisicao)*/
            }
            true
        }
        getCount(SQLiteHelper(this).countNotf(username))

        var correction = findViewById<View>(R.id.viewCorrection)

        /*var newConfig = Configuration()
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            navRail.visibility = VISIBLE
            correction.visibility = VISIBLE
            bottomNavigationView.visibility = GONE
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            navRail.visibility = GONE
            correction.visibility = GONE
            bottomNavigationView.visibility = VISIBLE
        }*/


        //updateBadge(this, username)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        /*var bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        var navRail = findViewById<NavigationRailView>(R.id.nav_viewRail)
        var correction = findViewById<View>(R.id.viewCorrection)*/


        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            /*navRail.visibility = VISIBLE
            correction.visibility = VISIBLE
            bottomNavigationView.visibility = GONE*/
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            /*navRail.visibility = GONE
            correction.visibility = GONE
            bottomNavigationView.visibility = VISIBLE*/
        }
    }

    fun restartFragment() {
        var mainMenu = Intent(this, Main_nav::class.java).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, username)
        }
        startActivity(mainMenu)
        finish()
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
        var navRail = findViewById<NavigationRailView>(R.id.nav_viewRail)
        var badge = bottomNavigationView.getOrCreateBadge(R.id.notificacoes)
        var badgeRail = navRail.getOrCreateBadge(R.id.notificacoes)
        if (count != 0) {
            badge.isVisible = true
            badgeRail.isVisible = true
            // An icon only badge will be displayed unless a number or text is set:
            badge.number = count
            badgeRail.number = count
        } else {
            badge.isVisible = false
            badgeRail.isVisible = false
        }
    }



    private fun setCurrentFragment(fragment: Fragment) {
        var name = fragment.toString().substring(0, 5)
        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        var navRail = findViewById<NavigationRailView>(R.id.nav_viewRail)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_activity_main_nav, fragment)
            commit()
        }
        if (name == "MainM"){
            bottomNavigationView.getMenu().findItem(R.id.menu).setChecked(true)
            navRail.getMenu().findItem(R.id.menu).setChecked(true)
        } else if (name == "Relat"){
            bottomNavigationView.getMenu().findItem(R.id.relatorioNav).setChecked(true)
            navRail.getMenu().findItem(R.id.relatorioNav).setChecked(true)
        } else if (name == "Notif"){
            bottomNavigationView.getMenu().findItem(R.id.notificacoes).setChecked(true)
            navRail.getMenu().findItem(R.id.notificacoes).setChecked(true)
        } else if (name == "Requi"){
            bottomNavigationView.getMenu().findItem(R.id.requisicoes).setChecked(true)
            navRail.getMenu().findItem(R.id.requisicoes).setChecked(true)
        }
    }
    public val mainNavContext = this
}