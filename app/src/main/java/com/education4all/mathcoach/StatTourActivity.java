package com.education4all.mathcoach;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.education4all.mathcoach.MathCoachAlg.StatisticMaker;

import java.util.ArrayList;
import MathCoachAlg.Task;


public class StatTourActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_tour);



        ActionBar myActionBar = getSupportActionBar();
        myActionBar.setDisplayHomeAsUpEnabled(true);

        int TourNumber =  getIntent().getIntExtra("Tour", -1);
        if (TourNumber >= 0 ) {
            ScrollView Tasks = (ScrollView) findViewById(R.id.scrollView2);
            LinearLayout justALayout = new LinearLayout(this);
            justALayout.setOrientation(LinearLayout.VERTICAL);
            ArrayList<String> deTour = StatisticMaker.loadTour(this, TourNumber).serialize();

            int skip = 0; // Непонятно как работающий костыль
            for (int i = 1; i < deTour.size() - 1; ++i) {
                TextView newTask = new TextView(this);
                ArrayList<String> answers = new ArrayList<String>();
                Task currentTask = new Task(deTour.get(i));
                ArrayList<String> TaskDepiction = Task.DepictTaskExtended(deTour.get(i), answers);

                for (int j = 0; j < TaskDepiction.size(); ++j) {
                    String output = TaskDepiction.get(j);
                    String testPart = ", i=" + Integer.toString(i) + " of " + Integer.toString(deTour.size())
                            + ", j=" + Integer.toString(j) + " of " + Integer.toString(TaskDepiction.size());
//                    output += testPart; // Вывод данных в тестовом режиме, TODO убрать
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



}
