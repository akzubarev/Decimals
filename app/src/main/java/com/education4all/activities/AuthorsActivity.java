package com.education4all.activities;

import static com.education4all.utils.Utils.versioningTool;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.education4all.BuildConfig;
import com.education4all.R;
import com.education4all.utils.Utils;

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
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView versionTV = findViewById(R.id.version);
            String versionTag = "";
            if (BuildConfig.BUILD_TYPE.equals("debug") || versioningTool().equals(Utils.VERSIONING_REMOVEFIREBASE))
                versionTag = " (beta)";
            String versionText = String.format("Версия: %s%s", versionName, versionTag).trim();
            versionTV.setText(versionText);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void goToWeb(View view) {
        Intent intent = new Intent(this, WebActivity.class);
        startActivity(intent);
        finish();
    }
}
