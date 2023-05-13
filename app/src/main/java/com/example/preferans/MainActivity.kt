package com.example.preferans

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import java.util.*

class MainActivity : AppCompatActivity() {
    private val viewModel: GameViewModel by viewModels()
    var currentLang: String = "sr"
    val currentLangLiveData = MutableLiveData<String>()
    val languageContext: MutableLiveData<Context> = MutableLiveData()

    override fun attachBaseContext(newBase: Context) {
        currentLang = loadLanguageFromPref(newBase)
        //currentLang = "sr"
        val newLocale = Locale(currentLang)
        val context = updateLocale(newBase, newLocale)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        languageContext.value = this
        // Initialize game with player names
        viewModel.createNewGame(
            listOf(
                Player("Alice"),
                Player("Bob"),
                Player("Charlie")
            )
        )
    }

    fun updateLocale(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
    /*fun switchLanguage(locale: Locale) {
        currentLang = locale.language
        Log.d("MainActivity", "Switching language to: $currentLang")
        saveLanguageToPref(currentLang,this)
        currentLangLiveData.postValue(currentLang)
        recreate()
    }*/
    fun switchLanguage(locale: Locale) {
        currentLang = locale.language
        saveLanguageToPref(currentLang,this)
        recreate()
    }
    fun saveLanguageToPref(lang: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("language", lang)
        editor.apply()
    }
    fun getCurrentLanguageContext(): Context {
        return updateLocale(this, Locale(currentLang))
    }

    fun loadLanguageFromPref(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("language", "sr") ?: "sr"
    }
}