package com.education4all.decimals;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
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

import org.w3c.dom.Text;

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

//        ViewGroup vg = (ViewGroup) tabs.getChildAt(0);
//        int tabsCount = vg.getChildCount();
//        for (int j = 0; j < tabsCount; j++) {
//            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
//            int tabChildsCount = vgTab.getChildCount();
//            for (int i = 0; i < tabChildsCount; i++) {
//                View tabViewChild = vgTab.getChildAt(i);
//                if (tabViewChild instanceof AppCompatTextView) {
//                    ((AppCompatTextView) tabViewChild).setTextSize(getResources().getDimension(R.dimen.dimen2) / getResources().getDisplayMetrics().density);
//                    //  ((AppCompatTextView) tabViewChild).setTextAppearance(R.style.TabText);
//                    //     Log.d("fuuuuuuck", String.valueOf(((AppCompatTextView) tabViewChild).getTextSize()));
//                }
//            }
//        }


//        tabs.getTabAt(1).setCustomView(R.layout.tab);
//        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                if (tab.getCustomView() instanceof TextView)
//                    ((TextView) (tab.getCustomView())).setTextColor(getResources().getColor(R.color.main));
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                if (tab.getCustomView() instanceof TextView)
//                    ((TextView) (tab.getCustomView())).setTextColor(getResources().getColor(R.color.shadowed));
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });


        Toolbar myToolbar = findViewById(R.id.my_toolbar);
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
            CommonOperations.FixDialog(dialog, getApplicationContext());
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

    @Override
    protected void onResume() {
        super.onResume();
    }

}
