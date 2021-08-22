package com.education4all.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.education4all.R;

public class AuthorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView donate = findViewById(R.id.donate);
        SpannableString ss = new SpannableString(getString(R.string.donatetext));
        ss.setSpan(new URLSpan(getString(R.string.donatelink)), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        donate.setText(ss);
        //  donate.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public void goToWeb(View view) {
        Intent intent = new Intent(this, WebActivity.class);
        startActivity(intent);
        finish();
    }
}
