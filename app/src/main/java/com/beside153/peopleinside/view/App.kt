package com.beside153.peopleinside.view

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.beside153.peopleinside.util.PreferenceUtil

class App : Application() {
    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
