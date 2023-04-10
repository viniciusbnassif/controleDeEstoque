package com.kingdom.controledeestoque

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kingdom.controledeestoque.ui.main.MenuPrincipalFragment

class Menu_Principal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu__principal)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MenuPrincipalFragment.newInstance())
                .commitNow()
        }
    }
}