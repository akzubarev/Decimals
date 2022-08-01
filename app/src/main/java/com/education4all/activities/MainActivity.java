package com.education4all.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.education4all.BuildConfig;
import com.education4all.R;
import com.education4all.utils.Enums;
import com.education4all.utils.Utils;
import com.education4all.firebase.FireBaseUtils;
import com.education4all.mathCoachAlg.DataReader;
import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tasks.Task;
import com.education4all.mathCoachAlg.tours.Tour;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    final String tasktype = BuildConfig.FLAVOR;
    FireBaseUtils fireBaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");
        Task.setType(tasktype);

        fireBaseUtils = new FireBaseUtils();
        fireBaseUtils.login(this, () -> {
            try {
                showStatitics();
            } catch (Exception e) {
                TextView statistics = findViewById(R.id.statistics);
                statistics.setText("\nИзменен формат записи результатов\nПерейдите на экран результатов для обновления");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        showSettings();
        disableButtons(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(this, AuthorsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

//        case R.id.action_update:
//        CommonOperations.updateStatistics(this);
//        break;
//        case R.id.action_trash:
//        StatisticMaker.removeStatistics(this);
//        break;
//        case R.id.action_generate:
//        CommonOperations.genereteFakeStatistics(this, false);
//        break;
//        case R.id.action_generateOLD:
//        CommonOperations.genereteFakeStatistics(this, true);
//        break;
//        case R.id.action_save:
//        CommonOperations.save(this);
//        break;
//        case R.id.action_load:
//        CommonOperations.load(this);
//        break;

    }

    private void disableButtons(boolean enabled) {
        findViewById(R.id.stat_button).setEnabled(enabled);
        findViewById(R.id.settings_button).setEnabled(enabled);
        findViewById(R.id.start_button).setEnabled(enabled);
    }

    public void startTasks(View view) {
        disableButtons(false);
        Intent intent = new Intent(this, TaskActivity.class);
        startActivity(intent);
    }

    public void startSettings(View view) {
        disableButtons(false);
        Intent intent = new Intent(this, SettingsMainActivity.class);
        startActivity(intent);
    }

    public void startStatistic(View view) {
        disableButtons(false);
        Intent intent = new Intent(this, StatiscticsActivity.class);
        // Intent intent = new Intent(this, MultiLineChartActivity.class);
        startActivity(intent);
    }

    private void showStatitics() throws Exception {
        TextView statistics = findViewById(R.id.statistics);
        String solvedText = "Сейчас 0, в среднем 0",
                daysText = "Сейчас 0, в среднем 0";
        int secondstoday = 0,
                goal = DataReader.GetInt(DataReader.GOAL, this) * 60;

        if (StatisticMaker.getTourCount(this) > 0) {
            int subsequentAnswers = 0, subsequentDays = 0;
            boolean lastAnswerStreakIsValid = false, lastDayStreakIsvalid = false;

            ArrayList<Integer> streakDays = new ArrayList<>();
            ArrayList<Integer> streakSolved = new ArrayList<>();

            String date = null, prevdate = null,
                    lastStreakEndDate = null;

            for (int tourNumber = 0; tourNumber < StatisticMaker.getTourCount(this); tourNumber++) {

                Tour tour = StatisticMaker.loadTour(this, tourNumber);

                if (tour.getTotalTasks() == 0)
                    throw new Exception("Старый формат данных");

                int seconds = (int) tour.getTourTime();
                date = tour.date();

                Enums.twodates relationOfDates = Enums.twodates.equal;

                if (tourNumber > 0)
                    relationOfDates = Enums.isSubsequent(prevdate, date);

                if (relationOfDates == Enums.twodates.equal)
                    secondstoday += seconds;
                else {
                    if (secondstoday >= goal)
                        subsequentDays++;
                    secondstoday = seconds;

                    if (relationOfDates != Enums.twodates.subsequent && subsequentDays > 0) {
                        streakDays.add(subsequentDays);
                        lastStreakEndDate = prevdate;
                        subsequentDays = 0;
                    }
                }

                for (Task task : tour.getTourTasks()) {

                    boolean right = task.correct();
                    if (right) {
                        subsequentAnswers++;
                        lastAnswerStreakIsValid = true;
                    } else {
                        if (subsequentAnswers != 0) {
                            streakSolved.add(subsequentAnswers);
                            subsequentAnswers = 0;
                            lastAnswerStreakIsValid = false;
                        }
                    }
                }
                prevdate = date;
            }

            if (secondstoday >= goal)
                subsequentDays++;

            if (subsequentDays != 0) {
                streakDays.add(subsequentDays);
                lastStreakEndDate = date;
            }
            if (subsequentAnswers != 0)
                streakSolved.add(subsequentAnswers);


            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());

            String today = sdf.format(c.getTime());
            if (streakDays.size() > 0)
                if (Enums.isSubsequent(lastStreakEndDate, today) != Enums.twodates.unrelated)
                    lastDayStreakIsvalid = true;

            if (Enums.isSubsequent(date, today) != Enums.twodates.equal)
                secondstoday = 0;

            int averageDays = 0, averageSolved = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                averageDays = (int) Math.round(streakDays.stream().mapToInt(val -> val).average().orElse(0));
                averageSolved = (int) Math.round(streakSolved.stream().mapToInt(val -> val).average().orElse(0));
            } else {
                if (!streakDays.isEmpty()) {
                    for (Integer streak : streakDays)
                        averageDays += streak;
                    averageDays = (int) Math.round(((double) averageDays) / streakDays.size());
                }

                if (!streakSolved.isEmpty()) {
                    for (Integer streak : streakSolved)
                        averageSolved += streak;

                    averageSolved = (int) Math.round(((double) averageSolved) / streakSolved.size());
                }
            }


            if (streakSolved.size() > 0)
                solvedText = String.format("Сейчас %d, в среднем %d",
                        lastAnswerStreakIsValid ? streakSolved.get(streakSolved.size() - 1) : 0, averageSolved);
            if (streakDays.size() > 0)
                daysText = (String.format("Сейчас %d, в среднем %d",
                        lastDayStreakIsvalid ? streakDays.get(streakDays.size() - 1) : 0, averageDays));
        }

        statistics.setText(String.format("Решено подряд:\n" +
                        "%s\n" +
                        "Дней подряд:\n" +
                        "%s",
                solvedText, daysText));


        showGoal(secondstoday, goal);
    }

    private void showSettings() {
        TextView settings = findViewById(R.id.settings_text);
        String text = "";

//        String[] operations = Task.getOperations();
//
//        for (int action = 0; action < 4; action++) {
//            String fragment = "";
//            if (BuildConfig.FLAVOR.equals("integers") && action == 3) {
//                for (int i = 0; i <= 3; ++i)
//                    if (i == 1) {
//                        if (DataReader.checkComplexity(3, 1, this) ||
//                                DataReader.checkComplexity(3, 4, this))
//                            fragment += 2 + ", ";
//                    } else if (DataReader.checkComplexity(action, i, this))
//                        fragment += (i + 1) + ", ";
//            } else {
//                for (int i = 0; i <= 3; ++i)
//                    if (DataReader.checkComplexity(action, i, this))
//                        fragment += (i + 1) + ", ";
//            }
//
//            if (!fragment.isEmpty()) {
//                fragment = fragment.substring(0, fragment.lastIndexOf(","));
//                if (action < 3)
//                    fragment += " ";
//
//                text += operations[action] + " " + fragment;
//            }
//        }
//
//        if (text.isEmpty())
//            text = "Задания не выбраны";
//
//        text += "\n";
        text += String.format("Длина раунда %d мин\n",
                DataReader.GetInt(DataReader.ROUND_TIME, this));
        int disapTime = DataReader.GetInt(DataReader.DISAP_ROUND_TIME, this);
        text += disapTime != -1 ?
                String.format("Условие исчезает через %d сек", disapTime)
                : "Условие не исчезает";

        settings.setText(text);
    }

    private void showGoal(int secondstoday, int goal) {
        TextView goaltext = findViewById(R.id.goal_text);
        goaltext.setText(String.format("Цель: %s/%s",
                DateUtils.formatElapsedTime(secondstoday),
                DateUtils.formatElapsedTime(goal)));
    }


}