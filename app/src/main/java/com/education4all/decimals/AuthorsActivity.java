package com.education4all.decimals;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AuthorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
