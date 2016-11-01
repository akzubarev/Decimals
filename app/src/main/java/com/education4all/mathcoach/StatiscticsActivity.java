package com.education4all.mathcoach;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.education4all.mathcoach.MathCoachAlg.StatisticMaker;

import MathCoachAlg.Tour;

public class StatiscticsActivity extends ActionBarActivity {
    private Context l_context = this;

    View.OnClickListener tourClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(l_context, StatTourActivity.class);
            i.putExtra("Tour", (Integer)(v.getTag()));
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statisctics);


        ActionBar myActionBar = getSupportActionBar();
        myActionBar.setDisplayHomeAsUpEnabled(true);

        int tourCount = StatisticMaker.getTourCount(this);
        ScrollView Tours = (ScrollView)findViewById(R.id.scrollView);
        LinearLayout justALayout = new LinearLayout(this);
        justALayout.setOrientation(LinearLayout.VERTICAL);
        View bar = new View(this);
        bar.setVisibility(View.VISIBLE);
        bar.setMinimumHeight(1);
        bar.setPadding(50,0,50,0);
        bar.setBackgroundColor(Color.DKGRAY);
        justALayout.addView(bar);
        Tours.addView(justALayout);
        for (int tourNumber = tourCount - 1; tourNumber >= 0; --tourNumber) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView newTour = new TextView(this);
            String tourInfo = StatisticMaker.getTourInfo(this,tourNumber);
            newTour.setText(Tour.DepictTour(tourInfo));
            newTour.setTag(tourNumber);
            newTour.setTextSize(20);
            newTour.setOnClickListener(tourClick);

            TextView arrow = new TextView((this));
            arrow.setTag(tourNumber);
            arrow.setOnClickListener(tourClick);
            arrow.setText("〉");
            arrow.setTextSize(20);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(120,120);
            layoutParams.gravity= Gravity.CENTER;
            arrow.setLayoutParams(layoutParams);
            arrow.setMinimumHeight(40);
            arrow.setMinimumWidth(40);
            row.addView(newTour);
            row.addView(arrow);
            justALayout.addView(row);

            bar = new View(this);
            bar.setVisibility(View.VISIBLE);
            bar.setMinimumHeight(1);
            bar.setPadding(50,0,50,0);
            bar.setBackgroundColor(Color.DKGRAY);
            justALayout.addView(bar);
        }
    }



    private void touClick(View view) {

    }

}
