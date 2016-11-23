package com.education4all.mathcoach;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.education4all.mathcoach.MathCoachAlg.StatisticMaker;

import java.util.ArrayList;
import MathCoachAlg.Task;


public class StatTourActivity extends AppCompatActivity {
    int TourNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_tour);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TourNumber =  getIntent().getIntExtra("Tour", -1);
        if (TourNumber >= 0 ) {
            ScrollView Tasks = (ScrollView) findViewById(R.id.scrollView2);
            LinearLayout justALayout = new LinearLayout(this);
            justALayout.setOrientation(LinearLayout.VERTICAL);
            ArrayList<String> deTour = StatisticMaker.loadTour(this, TourNumber).serialize();

            int skip = 0; // костыль
            for (int i = 1; i < deTour.size() - 1; ++i) {
                TextView newTask = new TextView(this);
                ArrayList<String> answers = new ArrayList<String>();
                Task currentTask = new Task(deTour.get(i));
                ArrayList<String> TaskDepiction = Task.DepictTaskExtended(deTour.get(i), answers);

// КОСТЫЛЬ (на самом деле сейчас в TaskActivity сохраняются лишние дубликаты строк, а здесь из них приходится отбирать нужные) TODO когда-нибудь поправить это
                for (int j = 0; j < TaskDepiction.size(); ++j) {
                    String output = TaskDepiction.get(j);
                    String testPart = ", i=" + Integer.toString(i) + " of " + Integer.toString(deTour.size())
                            + ", j=" + Integer.toString(j) + " of " + Integer.toString(TaskDepiction.size());
                    testPart += ", skip=" + skip;
//                    output += testPart; // Вывод данных в тестовом режиме, TODO убрать

                    if (0 == j && 0 < skip  && !answers.get(j).equals("\u2026")) { //
                        break;
//                    output += "_" + answers.get(j);
                    }
                    newTask.setText(output);

                    if (answers.get(j).equals(currentTask.answer)) {
                        newTask.setTextColor(Color.parseColor("#1B5E20"));
                        i += skip;
                        skip = 0;
                    }
                    else {
                        newTask.setTextColor(Color.GRAY);
                        ++skip;
                    }
                    newTask.setTextSize(20);
                    justALayout.addView(newTask);
                    newTask = new TextView(this);
                }

//  ИСХОДНЫЙ КОД
//                for (int j = 0; j < TaskDepiction.size(); ++j) {
//                    String output = TaskDepiction.get(j);
//                    String testPart = ", i=" + Integer.toString(i) + " of " + Integer.toString(deTour.size())
//                            + ", j=" + Integer.toString(j) + " of " + Integer.toString(TaskDepiction.size());
//                    output += testPart; // Вывод данных в тестовом режиме, TODO убрать
//                    newTask.setText(output);
//                    if (answers.get(j).equals(currentTask.answer)) {
//                        newTask.setTextColor(Color.parseColor("#1B5E20"));
//                    }
//                    else {
//                        newTask.setTextColor(Color.GRAY);
//                    }
//                    newTask.setTextSize(20);
//                    justALayout.addView(newTask);
//                    newTask = new TextView(this);
//                }

//                newTask.setText(Task.DepictTask(deTour.get(i)));
//                newTask.setTextSize(20);
//                justALayout.addView(newTask);
            }
            Tasks.addView(justALayout);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.stat_tour, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_tour:
                StatisticMaker.removeTour(this, TourNumber);
                Intent intent = new Intent(this, StatiscticsActivity.class);
                finish();
                startActivity(intent);
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}