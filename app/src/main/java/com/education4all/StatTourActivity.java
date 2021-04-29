package com.education4all;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.education4all.MathCoachAlg.StatisticMaker;
import com.education4all.MathCoachAlg.Tasks.Task;
import com.education4all.MathCoachAlg.Tours.Tour;

import java.util.ArrayList;


public class StatTourActivity extends AppCompatActivity {
    int TourNumber;
    final String tasktype = BuildConfig.FLAVOR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_tour);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        // myToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_trash));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TourNumber = getIntent().getIntExtra("Tour", -1);
        if (TourNumber >= 0) {
            ScrollView Tasks = findViewById(R.id.scrollView2);
            LinearLayout layout = findViewById(R.id.linear);
            Tour tourinfo = StatisticMaker.loadTour(this, TourNumber);
            ArrayList<String> deTour = tourinfo.serialize();


            String tourInfoStr = StatisticMaker.getTourInfo(this, TourNumber);
            String txt = Tour.DepictTour(tourInfoStr);
            int divider = txt.indexOf("Решено");
            String datetime = txt.substring(1, divider - 1);
            String date = datetime.substring(0, 10);

            String line = String.format("%s %d/%d (%d%%)",
                    getString(R.string.star), tourinfo.rightTasks, tourinfo.tourTasks.size(),
                    (int) (tourinfo.rightTasks * 100.0 / tourinfo.tourTasks.size()));
            getSupportActionBar().setTitle(line);

            int jump = 0; // раньше сохранялись лишние копии заданий, которые нужно пропускать
            for (int i = 1; i < deTour.size() - 1; ++i) {
                ArrayList<String> answers = new ArrayList<>();
                Task currentTask = Task.makeTask(deTour.get(i));
                //         FractionTask ft = null;
                ArrayList<String> TaskDepiction = Task.DepictTaskExtended(deTour.get(i), answers);

//                if (tasktype.equals("fractions"))
//                    ft = (FractionTask) currentTask;

                for (int j = 0; j < TaskDepiction.size(); ++j) {
                    LinearLayout row = new LinearLayout(this);
                    LinearLayout.inflate(this, R.layout.stat_tour_block, row);
                    TextView task = row.findViewById(R.id.task);

                    String output = TaskDepiction.get(j);
                    int ind = output.indexOf('(');
                    int end = output.indexOf(')');

                    TextView seconds =  row.findViewById(R.id.seconds);
                    boolean userAnswerIsCorrect = answers.get(j).equals(currentTask.getAnswer());
                    String taskAndUserAnswer = output.substring(0, ind - 2);

                    //   if (!tasktype.equals("fractions"))
                    task.setText(taskAndUserAnswer);
//                    else {
////                        LinearLayout.inflate(this, R.layout.stat_tour_block_fractions, row);
////
////                        TextView integer1 = row.findViewById(R.id.integer1),
////                                integer2 = row.findViewById(R.id.integer2),
////                                integerA = row.findViewById(R.id.integerA),
////                                fraction1 = row.findViewById(R.id.fraction1),
////                                fraction2 = row.findViewById(R.id.fraction2),
////                                fractionA = row.findViewById(R.id.fractionA),
////                                operation = row.findViewById(R.id.operation);
////                        seconds = row.findViewById(R.id.seconds);
////
////                        TextView[] tvs = new TextView[]{integer1, integer2, integerA,
////                                fraction1, fraction2, fractionA,
////                                operation};
////
//
//                        String answer = taskAndUserAnswer.split(" ")[4];
//                        int idx1 = answer.indexOf("[");
//                        int idx2 = answer.indexOf("]");
//                        String integer = answer.substring(0, idx1);
//                        String fraction = answer.substring(idx1 + 1, idx2);
//                        int idx3 = fraction.indexOf("/");
//                        Spannable fractionS;
//                        if (idx3 == -1)
//                            fractionS = new SpannableString("");
//                        else
//                            fractionS = FractionTask.makeFraction(fraction.substring(0, idx3), fraction.substring(idx3 + 1));
//
//                        SpannableStringBuilder out = new SpannableStringBuilder();
//                        out.append(ft.makeExpression()).append(integer).append(fractionS);
//
//
//                        task.setText(out, TextView.BufferType.SPANNABLE);
////                        integer1.setText(ints[0]);
////                        integer2.setText(ints[1]);
////                        integerA.setText(integer);
////
////                        fraction1.setText(fractions[0], TextView.BufferType.SPANNABLE);
////                        fraction2.setText(fractions[1], TextView.BufferType.SPANNABLE);
////                        fractionA.setText(fractionS, TextView.BufferType.SPANNABLE);
////                        operation.setText(((FractionTask) currentTask).getOperation());
//
////                        if (!userAnswerIsCorrect)
////                            for (TextView tv : tvs)
////                                tv.setTextColor(ContextCompat.getColor(this, R.color.shadowed));
//
//                    }

                    if (userAnswerIsCorrect)
                        task.setTextColor(ContextCompat.getColor(this, R.color.main));
                    else
                        task.setTextColor(ContextCompat.getColor(this, R.color.shadowed));

                    String userTime = output.substring(ind + 1, end) + ".";
                    seconds.setText(userTime);

                    if (CommonOperations.requiresKostyl(date)) {
//                    String testPart = "\ni=" + i + " of " + (deTour.size()-2)
//                            + ", j=" + (j+1) + " of " + TaskDepiction.size();
//                    testPart += ", jump=" + jump;
//                    taskAndUserAnswer += testPart; // Вывод данных в тестовом режиме
//                    task.setText(taskAndUserAnswer);

                        // костыль
                        boolean taskWasSkipped = answers.get(j).equals("\u2026");
                        if (userAnswerIsCorrect || taskWasSkipped) {
                            i += jump;
                            jump = 0;
                        } else
                            ++jump;
                        if (i + jump == deTour.size() - 2)
                            i += jump;
                    }

                    layout.addView(row);
                }
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stat_tour, menu);
        return true;
    }


    private void deleteTour() {
        StatisticMaker.removeTour(this, TourNumber);
        Intent intent = new Intent(this, StatiscticsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //finish();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_tour) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Удаление результатов")
                    .setMessage("Вы уверены? Это действие нельзя будет отменить.")

                    .setNegativeButton("Отменить", (dialog1, which) -> {
                        //finish();
                    })
                    .setPositiveButton("Удалить", (dialog12, which) -> {
//                        saveTaskStatistic(); //Текущее задание не записываем!
//                            StatisticMaker.saveTour(currentTour, context); // Результаты тура тут сохранять не нужно, они сохранятся при завершении раунда.
                        deleteTour();
                    })
                    .show();

            CommonOperations.FixDialog(dialog, getApplicationContext()); // почему-то нужно для планшетов
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}