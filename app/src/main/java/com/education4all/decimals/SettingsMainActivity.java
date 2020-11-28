package com.education4all.decimals;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.education4all.decimals.MathCoachAlg.DataReader;
import com.education4all.decimals.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

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
                (tab, position) -> tab.setText(position == 0 ? "Задания" : "Интерфейс")
        ).attach();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        boolean fromtask = intent.getBooleanExtra("FromTask", false);
        if (fromtask) {
            AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
//                    .setTitle("Ошибка")
                    .setMessage("Пожалуйста, сначала выберите виды заданий.")
                    .setCancelable(false)
                    .setPositiveButton("Ок", (dialog1, which) -> {
                        // finish();
                    })
                    .show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
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
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
