package com.education4all

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.education4all.mathCoachAlg.DataReader
import com.education4all.utils.Enums

class ThemedApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        //светлая и темная тема меняются местами для отображения темной в IDE
        val theme: Enums.Theme = Enums.Theme.Companion.convert(
            DataReader.GetInt(DataReader.THEME, applicationContext)
        )
        when (theme) {
            Enums.Theme.LIGHT ->                 //"Светлая"
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Enums.Theme.DARK ->                 //"Темная";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Enums.Theme.SYSTEM ->                 //"Системная";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}