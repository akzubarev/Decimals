package com.education4all.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.education4all.R;
import com.education4all.activities.tabs.SectionsPagerAdapter;
import com.education4all.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        SectionsPagerAdapter spa = new SectionsPagerAdapter(this);
        ViewPager2 viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(spa);
        TabLayout tabs = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> {
                    String name = position == 0 ? "Задания" : "Интерфейс";
                    int color = position == 0 ? getResources().getColor(R.color.main) :
                            getResources().getColor(R.color.shadowed);

                    tab.setCustomView(R.layout.tab); //TextView

                    if (tab.getCustomView() instanceof TextView) {
                        TextView tv = (TextView) tab.getCustomView();
                        tv.setText(name);
                        tv.setTextColor(color);
                    }
                }
        ).attach();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() instanceof TextView)
                    ((TextView) (tab.getCustomView())).setTextColor(getResources().getColor(R.color.main));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() instanceof TextView)
                    ((TextView) (tab.getCustomView())).setTextColor(getResources().getColor(R.color.shadowed));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        boolean fromtask = intent.getBooleanExtra("FromTask", false);
        if (fromtask) {
            AlertDialog dialog = new AlertDialog.Builder(this)//, R.style.AlertDialogTheme)
//                    .setTitle("Ошибка")
                    .setMessage("Пожалуйста, сначала выберите виды заданий.")
                    .setCancelable(false)
                    .setPositiveButton("Ок", (dialog1, which) -> {
                        // finish();
                    }).create();
            dialog.show();
            Utils.FixDialog(dialog, getApplicationContext()); // почему-то нужно для планшетов
        }
    }


    public void startCompl(View view) {
        Intent intent = new Intent(this, ComplexityActivity.class);
        int p = Integer.parseInt((String) view.getTag());
        intent.putExtra("Type", p);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_settings, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_logout) {
//            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//            return true;
//        } else
//            return super.onOptionsItemSelected(item);
//    }
}
