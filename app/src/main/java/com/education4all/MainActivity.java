package com.education4all;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.education4all.MathCoachAlg.DataReader;
import com.education4all.MathCoachAlg.StatisticMaker;
import com.education4all.MathCoachAlg.Tasks.Task;
import com.education4all.MathCoachAlg.Tours.Tour;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    final String tasktype = BuildConfig.FLAVOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        // myToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_overflow));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");
        Task.setType(tasktype);
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.abs_layout);

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            showStatitics();
        } catch (Exception e) {
            TextView statistics = findViewById(R.id.statistics);
            statistics.setText("Ошибка\nИзменен формат записи результатов\nУдалите результаты для возможности подсчета статистики");
        }
        showSettings();

//        TextView statistics = findViewById(R.id.statistics);
//        SpannableString ss = new SpannableString("Разные шрифты");
//
//        Typeface font = Typeface.DEFAULT_BOLD,
//                font2 = Typeface.MONOSPACE;
//
//        ss.setSpan(new CustomTypefaceSpan("", font2), 0, 6, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//        ss.setSpan(new CustomTypefaceSpan("", font), 6, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//        statistics.setText(ss);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(this, AuthorsActivity.class);
            startActivity(intent);
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    public void startTasks(View view) {
        Intent intent = new Intent(this, TaskActivity.class);
        startActivity(intent);
    }

    public void startSettings(View view) {
        Intent intent = new Intent(this, SettingsMainActivity.class);
        startActivity(intent);
        //  genereteFakeStatistics();
        showStatitics();
    }

    public void startStatistic(View view) {
        Intent intent = new Intent(this, StatiscticsActivity.class);
        // Intent intent = new Intent(this, MultiLineChartActivity.class);
        startActivity(intent);
    }

    private void showStatitics() {
        TextView statistics = findViewById(R.id.statistics);
        String solvedText = "Сейчас 0, в среднем 0",
                daysText = "Сейчас 0, в среднем 0";
        int secondstoday = 0,
                goal = DataReader.GetValue("Goal", this) * 60;

        if (StatisticMaker.getTourCount(this) > 0) {
            int subsequentAnswers = 0, subsequentDays = 0;
            boolean lastAnswerStreakIsValid = false, lastDayStreakIsvalid = false;


            ArrayList<Integer> streakDays = new ArrayList<>();
            ArrayList<Integer> streakSolved = new ArrayList<>();

            String date = null, prevdate = null,
                    lastStreakEndDate = null;

            for (int tourNumber = 0; tourNumber < StatisticMaker.getTourCount(this); tourNumber++) {
                // String debug = "";
                String tourInfoStr = StatisticMaker.getTourInfo(this, tourNumber);
                String txt = Tour.DepictTour(tourInfoStr);

                int divider = txt.indexOf("Решено");
                int seconds = txt.indexOf("сек") + "сек".length();
                seconds = Integer.parseInt(txt.substring(seconds));

                String datetime = txt.substring(1, divider - 1);
                date = datetime.substring(0, 10);


                twodates relationOfDates = twodates.equal;
                if (tourNumber > 0)
                    relationOfDates = isSubsequent(prevdate, date);

                if (relationOfDates == twodates.equal)
                    secondstoday += seconds;
                else {
                    if (secondstoday >= goal)
                        subsequentDays++;
                    secondstoday = seconds;

                    if (relationOfDates != twodates.subsequent && subsequentDays > 0) {
                        streakDays.add(subsequentDays);
                        lastStreakEndDate = prevdate;
                        subsequentDays = 0;
                    }
                }


                Tour tourinfo = StatisticMaker.loadTour(this, tourNumber);
                ArrayList<String> deTour = tourinfo.serialize();


                int jump = 0; // костыль из прошлых версий
                for (int i = 1; i < deTour.size() - 1; ++i) {
                    ArrayList<String> answers = new ArrayList<>();
                    Task currentTask = Task.makeTask(deTour.get(i));
                    ArrayList<String> TaskDepiction = Task.DepictTaskExtended(deTour.get(i), answers);

                    //начало костыля
                    if (CommonOperations.requiresKostyl(date))
                        for (int j = 0; j < TaskDepiction.size(); ++j) {
                            boolean answerIsCorrect = answers.get(j).equals(currentTask.getAnswer());
                            boolean taskWasSkipped = answers.get(j).equals("\u2026");
                            if (answerIsCorrect || taskWasSkipped) {
                                i += jump;
                                jump = 0;
                            } else {
                                ++jump;
                            }
                            if (i + jump == deTour.size() - 2) {
                                i += jump;
                            }
                        }
                    //конец костыля

                    // debug += answers.get(answers.size() - 1).equals(currentTask.answer) ? "1 " : "0 ";
                    boolean right = answers.get(0).equals(currentTask.getAnswer());

                    if (right) {
                        subsequentAnswers++;
                        lastAnswerStreakIsValid = true;
                    } else {
                        if (subsequentAnswers != 0) {
                            //   Log.d("fuckkkkkk", String.valueOf(subsequentAnswers));
                            streakSolved.add(subsequentAnswers);
                            subsequentAnswers = 0;
                            lastAnswerStreakIsValid = false;
                        }
                        if (answers.get(answers.size() - 1).equals(currentTask.getAnswer())) {
                            subsequentAnswers++;
                            lastAnswerStreakIsValid = true;
                        }
                    }

                }

                // Log.d("fuckkkkkk", debug);
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
                if (isSubsequent(lastStreakEndDate, today) != twodates.unrelated)
                    lastDayStreakIsvalid = true;

            if (isSubsequent(date, today) != twodates.equal)
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

        Map<Integer, String> map = new HashMap<>();
        String[] operations = Task.getOperations();
        map.put(0, operations[0]);
        map.put(1, operations[1]);
        map.put(2, operations[2]);
        map.put(3, operations[3]);

        for (int action = 0; action < 4; action++) {
            String fragment = "";
            for (int i = 0; i <= 3; ++i)
                if (DataReader.checkComplexity(action, i, this))
                    fragment += (i + 1) + ", ";

            if (BuildConfig.FLAVOR.equals("integers") && action == 3)
                if (DataReader.checkComplexity(action, 4, this))
                    fragment += "" + 5;


            if (!fragment.isEmpty()) {
                fragment = fragment.substring(0, fragment.lastIndexOf(","));
                if (action < 3)
                    fragment += " ";
                text += map.get(action) + " " + fragment;
            }
        }

        if (text.isEmpty())
            text = "Задания не выбраны";

        text += "\n";
        text += String.format("Длина раунда %d мин\n", DataReader.GetValue("RoundTime", this));
        int disapTime = DataReader.GetValue("DisapRoundTime", this);
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

    enum twodates {
        equal,
        subsequent,
        unrelated
    }

    private twodates isSubsequent(String prevdate, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(prevdate));
            c2.setTime(sdf.parse(date));
            if (c1.equals(c2))
                return twodates.equal;
            c1.add(Calendar.DATE, 1);
            if (c1.equals(c2))
                return twodates.subsequent;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return twodates.unrelated;
    }
}