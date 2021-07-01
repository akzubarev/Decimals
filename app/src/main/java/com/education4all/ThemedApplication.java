package com.education4all;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.education4all.MathCoachAlg.DataReader;
import com.education4all.MathCoachAlg.StatisticMaker;

public class ThemedApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
       // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        //светлая и темная тема меняются местами для отображения темной в IDE
        switch (DataReader.GetValue("Theme", getApplicationContext())) {
            case 1:
                //"Светлая"
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case -1:
            default:
                //"Темная";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 0:
                //"Системная";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}