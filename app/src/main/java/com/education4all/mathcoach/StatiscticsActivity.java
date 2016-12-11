package com.education4all.mathcoach;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            RelativeLayout row = new RelativeLayout(this);

            TextView newTour = new TextView(this);
            newTour.setId(tourNumber);
            String tourInfo = StatisticMaker.getTourInfo(this,tourNumber);
            newTour.setText(Tour.DepictTour(tourInfo));
            newTour.setTag(tourNumber);
            newTour.setTextSize(20);
            newTour.setOnClickListener(tourClick);

            Button arrow = new Button((this));
            arrow.setTag(tourNumber);
            arrow.setOnClickListener(tourClick);
//            arrow.setText("\u232A");
            arrow.setText("〉");
            arrow.setTextSize(20);
            arrow.setTextColor(Color.WHITE);
            arrow.setBackgroundColor(Color.TRANSPARENT);
            arrow.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_TOP, newTour.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, newTour.getId());
            arrow.setLayoutParams(layoutParams);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_stats:
                finish();
                StatisticMaker.removeStatistics(this);
                Intent intent = getIntent();
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
