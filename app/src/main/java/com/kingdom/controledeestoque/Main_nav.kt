package com.kingdom.controledeestoque

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.kingdom.controledeestoque.databinding.ActivityMainNavBinding

class Main_nav : AppCompatActivity() {

    private lateinit var binding: ActivityMainNavBinding
    lateinit var username : String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)

        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }


        //bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)

        username = intent.getStringExtra(AlarmClock.EXTRA_MESSAGE)!!
        //username = Intent().getStringExtra(AlarmClock.EXTRA_MESSAGE)!!


        val MainMenu=MainMenu(username)
        val Relatorio=Relatorio()
        val Notificacoes=Notificacoes(username, this)


        setCurrentFragment(MainMenu)

        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            getCount(SQLiteHelper(this).countNotf(username))
            when(it.itemId){
                R.id.menu->setCurrentFragment(MainMenu)
                R.id.relatorioNav->setCurrentFragment(Relatorio)
                R.id.notificacoes->setCurrentFragment(Notificacoes)
            }
            true
        }
        getCount(SQLiteHelper(this).countNotf(username))


        //updateBadge(this, username)
    }
    fun getCount(count: Int){
        updateBadge(count)
    }
    fun updateBadge(count: Int) {
        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        if (count != 0) {
            var badge = bottomNavigationView.getOrCreateBadge(R.id.notificacoes)
            badge.isVisible = true
            // An icon only badge will be displayed unless a number or text is set:
            badge.number = count
        }
    }



    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_activity_main_nav,fragment)
            commit()
        }
    public val mainNavContext = this
}