package com.education4all;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.education4all.mathCoachAlg.DataReader;
import com.education4all.utils.Enums;
import com.education4all.utils.Utils;

public class ThemedApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        //светлая и темная тема меняются местами для отображения темной в IDE
        Enums.Theme theme = Enums.Theme.convert(
                DataReader.GetInt(DataReader.THEME, getApplicationContext())
        );

        switch (theme) {
            case LIGHT:
                //"Светлая"
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case DARK:
            default:
                //"Темная";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case SYSTEM:
                //"Системная";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}