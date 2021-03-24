package com.education4all.decimals;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.education4all.decimals.MathCoachAlg.DataReader;
import com.education4all.decimals.MathCoachAlg.StatisticMaker;
import com.education4all.decimals.MathCoachAlg.Task;
import com.education4all.decimals.MathCoachAlg.Tour;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        // myToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_overflow));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.abs_layout);

    }

    public void workWithFractions() {
//        TextView decimal = findViewById(R.id.decimal);
//        TextView integer = findViewById(R.id.integer);
//        SpannableString integertext = new SpannableString("3");
//        integertext = resizeeverythingRel(integertext, 2);
//        integer.setText(integertext);
//        decimal.setText(Html.fromHtml("<sup>1</sup>/<sub>2</sub>"), TextView.BufferType.SPANNABLE);
    }

    public SpannableString resizeeverythingRel(SpannableString spannable, int increase) {
        spannable.setSpan(new RelativeSizeSpan(increase), 0, spannable.length(), 0);
        return spannable;
    }

    public SpannableString resizefractionRel(SpannableString spannable, String target, int increase) {

        int startindex = spannable.toString().indexOf(target);
        spannable.setSpan(new RelativeSizeSpan(increase), startindex, startindex + target.length(), 0);
        return spannable;
    }


    @Override
    protected void onResume() {
        super.onResume();
        showStatitics();
        showSettings();
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
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent intent = new Intent(this, AuthorsActivity.class);
                startActivity(intent);
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
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

                int jump = 0; // костыль из StatTourActivity
                for (int i = 1; i < deTour.size() - 1; ++i) {
                    ArrayList<String> answers = new ArrayList<String>();
                    Task currentTask = new Task(deTour.get(i));
                    ArrayList<String> TaskDepiction = Task.DepictTaskExtended(deTour.get(i), answers);

                    //костыль
                    for (int j = 0; j < TaskDepiction.size(); ++j) {
                        boolean answerIsCorrect = answers.get(j).equals(currentTask.answer);
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
                    if (answers.get(0).equals(currentTask.answer)) {
                        subsequentAnswers++;
                        lastAnswerStreakIsValid = true;
                    } else {
                        if (subsequentAnswers != 0) {
                            //   Log.d("fuckkkkkk", String.valueOf(subsequentAnswers));
                            streakSolved.add(subsequentAnswers);
                            subsequentAnswers = 0;
                            lastAnswerStreakIsValid = false;
                        }
                        if (answers.get(answers.size() - 1).equals(currentTask.answer)) {
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
        map.put(0, Task.operations[0]);
        map.put(1, Task.operations[1]);
        map.put(2, Task.operations[2]);
        map.put(3, Task.operations[3]);

        for (int action = 0; action < 4; action++) {
            String fragment = "";
            for (int i = 0; i <= 3; ++i)
                if (DataReader.checkComplexity(action, i, this))
                    fragment += (i + 1) + ", ";

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

    Random rnd = new Random();

    public void genereteFakeStatistics() {


        String[] stats = new String[]{
                "1 0 0 1 1 1 1 1 1 1 1 0",
                "2 1 1 1 1 1 1 1 1 1 0 1",
                "3 1 1 1 1 1 1 1 0 1 1 1",
                "4 1 0 1 1 1 1 1 1 0 1 1",
                "5 1 0 1 1 1 1 0 1 1 1 1",
                "6 1 1 1 1 1 1 1 0 1 1 1",
                "7 0 1 1 1 1 1 1 1 1 0 1",
                "8 1 0 1 1 0 1 1 1 1 1 0",
                "9 1 1 1 1 1 1 1 1 1 1 0",
                "10 1 1 1 1 1 0 1 1 1 0 0"
        };

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c = Calendar.getInstance();

        for (String stat : stats) {
            c.setTime(new Date());
            c.add(Calendar.DATE, -31);
            String[] lines = stat.split(" ");
            c.add(Calendar.DATE, Integer.parseInt(lines[0]));

            Tour tour = new Tour();
            tour.tourDateTime = c.getTimeInMillis();
            tour.tourTime = 0;

            for (int j = 1; j < lines.length; j++) {
                Task task = new Task();
                task.timeTaken = rnd.nextInt(100);
                tour.tourTime += task.timeTaken;

//                for (int k = 0; k < rnd.nextInt(4); k++) {
//                    int wronganswer = 0;
//                    do {
//                        wronganswer = rnd.nextInt(20);
//                    } while (String.valueOf(wronganswer).equals(task.answer));
//                    task.userAnswer += wronganswer + ":" + rnd.nextInt(20) + '|';
//                }

                if (Integer.parseInt(lines[j]) == 1) {
                    task.userAnswer += task.answer + ":" + rnd.nextInt(20) + '|';
                    tour.rightTasks++;
                } else
                    task.userAnswer += "\u2026" + ":" + rnd.nextInt(20) + '|';

                tour.tourTasks.add(task);
            }

            tour.totalTasks = tour.tourTasks.size();
            StatisticMaker.saveTour(tour, this);
        }

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