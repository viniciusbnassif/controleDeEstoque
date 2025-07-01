package com.kingdom.controledeestoque

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    // Salvar dados
    fun saveData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply() // Salva de forma assíncrona
    }

    // Recuperar dados
    fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    // Salvar dados Boolean
    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    // Recuperar Boolean
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Limpar uma chave específica
    fun clearKey(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    // Limpar todos os dados
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
