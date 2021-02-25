package com.education4all.decimals;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.gridlayout.widget.GridLayout;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.education4all.decimals.MathCoachAlg.StatisticMaker;

import com.education4all.decimals.MathCoachAlg.Tour;

public class StatiscticsActivity extends AppCompatActivity {
    private Context l_context = this;

    View.OnClickListener tourClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(l_context, StatTourActivity.class);
            i.putExtra("Tour", (Integer) (v.getTag()));
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statisctics);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        // myToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_trash));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        int tourCount = StatisticMaker.getTourCount(this);
        ScrollView Tours = (ScrollView) findViewById(R.id.scrollView);
        LinearLayout justALayout = new LinearLayout(this);
        justALayout.setOrientation(LinearLayout.VERTICAL);

        View bar = new View(this);
        bar.setVisibility(View.VISIBLE);
        bar.setMinimumHeight(5);
        bar.setBackgroundColor(getResources().getColor(R.color.shadowed));
        bar.setPadding(50, 0, 50, 0);
        justALayout.addView(bar);
        Tours.addView(justALayout);

        for (int tourNumber = tourCount - 1; tourNumber >= 0; --tourNumber) {
            RelativeLayout row = new RelativeLayout(this);

            String tourInfoStr = StatisticMaker.getTourInfo(this, tourNumber);
            String txt = Tour.DepictTour(tourInfoStr);
            Typeface font = Typeface.create("sans-serif-light", Typeface.NORMAL);

            boolean isAllTasksRight = (txt.substring(0, 1).equals("="));
            int divider = txt.indexOf("Решено");
            String datetime = txt.substring(1, divider - 1);
            String info;
            if (!isAllTasksRight)
                info = txt.substring(divider);
            else
                info = getString(R.string.star) + " " + txt.substring(divider);
            // info = "★ " + txt.substring(divider);

            TextView tourdatetime = new TextView(this);
            tourdatetime.setId(tourNumber + 1);
            tourdatetime.setText(datetime);
            tourdatetime.setTag(tourNumber);
            tourdatetime.setTextSize(getResources().getDimension(R.dimen.dimen5) / getResources().getDisplayMetrics().density);
            tourdatetime.setOnClickListener(tourClick);
            tourdatetime.setGravity(Gravity.TOP | Gravity.START);
            tourdatetime.setTypeface(font);

            TextView tourinfo = new TextView(this);
            // tourinfo.setId(tourNumber);
            tourinfo.setText(info);
            tourinfo.setTextSize(getResources().getDimension(R.dimen.dimen4) / getResources().getDisplayMetrics().density);
            tourinfo.setOnClickListener(tourClick);
            tourinfo.setTag(tourNumber);
            tourinfo.setGravity(Gravity.BOTTOM | Gravity.START);
            tourinfo.setTypeface(font);


            Button arrow = new Button((this));
            arrow.setTag(tourNumber);
            arrow.setOnClickListener(tourClick);
//            arrow.setText("\u232A");
            //arrow.setText("@strings/Arrow");
            //arrow.setText("›");
            arrow.setText(getString(R.string.arrow));
            //  arrow.setPadding(0, 0, 0, 10);
            arrow.setTextSize(getResources().getDimension(R.dimen.dimen1) / getResources().getDisplayMetrics().density);
            arrow.setBackgroundColor(Color.TRANSPARENT);
            arrow.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
            arrow.setTextColor(ContextCompat.getColor(this, R.color.additional));

            row.setGravity(Gravity.CENTER_VERTICAL);

            tourdatetime.setTextColor(ContextCompat.getColor(this, R.color.main));
            tourinfo.setTextColor(ContextCompat.getColor(this, R.color.main));

//            if (isAllTasksRight)
//                tourinfo.setTypeface(tourinfo.getTypeface(), Typeface.ITALIC);


            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            arrow.setLayoutParams(layoutParams);


            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.BELOW, tourdatetime.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (!isAllTasksRight)
                layoutParams.leftMargin = 50;
            tourinfo.setLayoutParams(layoutParams);

            row.addView(tourdatetime);
            row.addView(tourinfo);
            row.addView(arrow);
            justALayout.addView(row);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 20, 0, 20);
            row.setLayoutParams(params);

            bar = new View(this);
            bar.setVisibility(View.VISIBLE);
            bar.setMinimumHeight(5);
            bar.setPadding(50, 0, 50, 0);
            bar.setBackgroundColor(getResources().getColor(R.color.shadowed));
            justALayout.addView(bar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.statisctics, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public void DeleteStatistics() {
        finish();
        StatisticMaker.removeStatistics(this);
        Intent intent = getIntent();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_stats:
                if (StatisticMaker.getTourCount(this) > 0) {
                    AlertDialog dialog = new AlertDialog.Builder(StatiscticsActivity.this)
                            .setTitle("Удаление результатов")
                            .setMessage("Вы уверены? Это действие нельзя будет отменить.")

                            .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //finish();
                                }
                            })
                            .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    DeleteStatistics();
                                }
                            })
                            .show();

                    CommonOperations.FixDialog(dialog, getApplicationContext());
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
