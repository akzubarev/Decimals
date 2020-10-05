package com.education4all.mathcoach;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.education4all.mathcoach.MathCoachAlg.StatisticMaker;

import java.util.ArrayList;
import com.education4all.mathcoach.MathCoachAlg.Task;


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

// КОСТЫЛЬ (на самом деле сейчас в TaskActivity сохраняются лишние дубликаты строк, а здесь из них приходится отбирать нужные) TODO когда-нибудь поправить это
            int jump = 0; // костыль
            for (int i = 1; i < deTour.size() - 1; ++i) {
                ArrayList<String> answers = new ArrayList<String>();
                Task currentTask = new Task(deTour.get(i));
                ArrayList<String> TaskDepiction = Task.DepictTaskExtended(deTour.get(i), answers);

                for (int j = 0; j < TaskDepiction.size(); ++j) {
                    TextView newTask = new TextView(this);
                    TextView userTimeTV = new TextView(this);
                    LinearLayout row = new LinearLayout(this);
                    row.setOrientation(LinearLayout.HORIZONTAL);

                    String output = TaskDepiction.get(j);
                    int ind = output.indexOf('(');
                    String taskAndUserAnswer = output.substring(0, ind - 2);
                    int end = output.indexOf(')');
                    String userTime = output.substring(ind + 1, end)  + ".";

                    String testPart = ", i=" + Integer.toString(i) + " of " + Integer.toString(deTour.size())
                            + ", j=" + Integer.toString(j) + " of " + Integer.toString(TaskDepiction.size());
                    testPart += ", jump=" + jump;
//                    taskAndUserAnswer += testPart; // Вывод данных в тестовом режиме, TODO закомментировать перед релизом

                    newTask.setText(taskAndUserAnswer);
                    newTask.setTextSize(20);
                    userTimeTV.setText(userTime);
                    userTimeTV.setGravity(Gravity.END);
                    userTimeTV.setTextSize(20);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    userTimeTV.setLayoutParams(layoutParams);

                    boolean userAnswerIsCorrect = answers.get(j).equals(currentTask.answer);
                    if (userAnswerIsCorrect) {
//                        int clr = Color.parseColor("#1B5E20");
                        int clr = Color.GREEN;
                        newTask.setTextColor(clr);
                        userTimeTV.setTextColor(clr);
                    }
                    else {
                        newTask.setTextColor(Color.LTGRAY);
                        userTimeTV.setTextColor(Color.LTGRAY);
                    }

                    // костыль
                    boolean taskWasSkipped = answers.get(j).equals("\u2026");
                    if (userAnswerIsCorrect || taskWasSkipped) {
                        i += jump;
                        jump = 0;
                    }
                    else {
                        ++jump;
                    }
                    if (i + jump == deTour.size() - 2) {
                        i += jump;
                    }

                    row.addView(newTask);
                    row.addView(userTimeTV);
                    justALayout.addView(row);
                }
;//ИСХОДНЫЙ КОД
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

    private void deleteTour()
    {

        StatisticMaker.removeTour(this, TourNumber);
        Intent intent = new Intent(this, StatiscticsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
        // User chose the "Settings" item, show the app settings UI...
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_tour:

                new AlertDialog.Builder(StatTourActivity.this)
                        .setTitle("Удаление результатов")
                        .setMessage("Вы уверены? Это действие нельзя будет отменить.")

                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                        saveTaskStatistic(); //Текущее задание не записываем!
//                            StatisticMaker.saveTour(currentTour, context); // Результаты тура тут сохранять не нужно, они сохранятся при завершении раунда.
                                deleteTour();
                            }
                        })
                        .show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}