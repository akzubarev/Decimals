package com.education4all.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.gridlayout.widget.GridLayout;

import com.education4all.BuildConfig;
import com.education4all.R;
import com.education4all.mathCoachAlg.DataReader;
import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tasks.Fraction;
import com.education4all.mathCoachAlg.tasks.Task;
import com.education4all.mathCoachAlg.tours.TaskQueue;
import com.education4all.mathCoachAlg.tours.Tour;
import com.education4all.utils.Enums;
import com.education4all.utils.Utils;

import java.util.Calendar;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {
    final String tasktype = BuildConfig.FLAVOR;
    Mode mode = Mode.integer;

    private final Handler progressBarHandler = new Handler(),
            taskDisapHandler = new Handler();

    private final Tour currentTour = new Tour();
    private Task task, correctionwork = null;
    TaskQueue taskQueue;

    private String answer = "",
            answerI = "", // текущая целая часть ответа
            answerT = "", // текущий числитель ответа
            answerB = "", // текущий знаменатель ответа
            answerF = ""; // текущая дробь ответа

    private TextView timerText;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private final Context context = this;
    private boolean answerShown = false, showTask = true;

    private float disapTime;
    long prevTaskTime, prevAnswerTime, tourStartTime;
    int tourLenght, secondsFromStart;
    private Thread progressthread, timerthread;

    int solved = 0, shown = 1;
    boolean answerWasShown = false;


    enum Mode {
        integer,
        top,
        bottom
    }


    String[] fractionSyms = new String[]{"□⁄□", "■⁄□", "□⁄■"};
    SpannableString[] fractionSymsSpan = new SpannableString[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
        //заполняем GridLayout
        GridLayout gl = findViewById(R.id.buttonsLayout);
        fillView(gl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int size = (int) getResources().getDimension(R.dimen.dimen0) / 3;
        for (int i = 0; i < fractionSyms.length; i++) {

            fractionSymsSpan[i] = SpannableString.valueOf(fractionSyms[i]);
            fractionSymsSpan[i].setSpan(new SuperscriptSpan(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            fractionSymsSpan[i].setSpan(new AbsoluteSizeSpan(size), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // fractionSymsSpan[i].setSpan(new SubscriptSpan(), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            fractionSymsSpan[i].setSpan(new AbsoluteSizeSpan(size), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        //обнуляем переменные и инициализируем элементы layout
        // массив разрешённых заданий
        int[][] allowedTasks = DataReader.readAllowedTasks(this);
        if (!Task.areTasks(allowedTasks)) {
            finish();
            startActivity(
                    new Intent(context, SettingsMainActivity.class)
                            .putExtra("FromTask", true)
            );
        } else {
            answer = "";
            handleTime();

            taskQueue = new TaskQueue(allowedTasks, context);
            if (!DataReader.GetBoolean(DataReader.QUEUE, context))
                taskQueue.setEnabled(false);

            Task.setAllowedTasks(allowedTasks);
            task = taskQueue.newTask();

            textViewUpdate();
        }
    }

    private void handleTime() {

        tourStartTime = System.currentTimeMillis();
        prevTaskTime = tourStartTime;
        prevAnswerTime = tourStartTime;
        tourLenght = DataReader.GetInt(DataReader.ROUND_TIME, this) * 60;

        Enums.TimerState timerstate = Enums.TimerState.convert(
                DataReader.GetInt(DataReader.TIMER_STATE, this));

        progressBar = findViewById(R.id.taskProgress);
        progressBar.setProgress(0);
        progressBar.setVisibility(timerstate == Enums.TimerState.INVISIBlE ?
                View.INVISIBLE : View.VISIBLE);

        progressthread = new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 1;
                if (timerstate == Enums.TimerState.CONTINIOUS)
                    progressBarHandler.post(() -> progressBar.setProgress(progressStatus));
                try {
                    Thread.sleep((tourLenght * 10L));
                } catch (InterruptedException e) {
                    return;
                }
            }
        });


        timerText = findViewById(R.id.timertext);
        timerText.setVisibility(timerstate == Enums.TimerState.INVISIBlE ?
                View.INVISIBLE : View.VISIBLE);
        timerText.setText(timeString(secondsFromStart, tourLenght));

        timerthread = new Thread(() -> {
            while (secondsFromStart < tourLenght) {
                secondsFromStart += 1;
                if (timerstate == Enums.TimerState.CONTINIOUS)
                    progressBarHandler.post(() -> timerText.setText(timeString(secondsFromStart, tourLenght)));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

//        if (timerstate == Enums.TimerState.CONTINIOUS) {
        timerthread.start();
        progressthread.start();
//        }

        disapTime = DataReader.GetInt(DataReader.DISAP_ROUND_TIME, this);
        resetDissapearing();
    }

    // формирует вывод о прошедшем времени
    private String timeString(int count, int max) {
        return String.format(Locale.getDefault(), "%s/%s",
                DateUtils.formatElapsedTime(count),
                DateUtils.formatElapsedTime(max));
    }

    //обновляет текстовый и визуальный таймеры
    void updateTimers() {
        progressBar.setProgress(progressStatus);
        timerText.setText(timeString(secondsFromStart, tourLenght));
    }

    //делаем все кнопки одинакового размера внутри GridLayout
    @SuppressLint("ClickableViewAccessibility")
    public void fillView(GridLayout parent) {
        //Button child;
        Button sym;
        switch (tasktype) {
            case "integers":
                sym = findViewById(R.id.Button_sym);
                sym.setText("");
                sym.setTag("empty");
                sym.setOnClickListener(null);
                sym.setOnLongClickListener(null);
                sym.setOnTouchListener(null);
                View linesym = findViewById(R.id.Line_sym);
                linesym.setBackgroundColor(Color.TRANSPARENT);
                break;
            case "fractions":
                sym = findViewById(R.id.Button_sym);
                sym.setTag("/");
                setModeSym();
                break;
            case "decimals":
            default:
//                sym.Text = ",";
//                sym.Tag = ",";
                break;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            RelativeLayout childLayout = (RelativeLayout) parent.getChildAt(i);
            View child = childLayout.getChildAt(0);
            View line = childLayout.getChildAt(1);
            if (child.getClass() == AppCompatButton.class && !child.getTag().equals("empty")) {
                if (child.getTag().equals("del"))
                    child.setOnLongClickListener(v -> {
                        answer = "";
                        textViewUpdate();
                        return true;
                    });
                else
                    child.setOnLongClickListener(v -> {
                        numberPress(v);
                        return true;
                    });

                child.setOnTouchListener((v, event) -> {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            line.setBackgroundColor(getResources().getColor(R.color.main));
                            break;
                        case MotionEvent.ACTION_UP:
                            line.setBackgroundColor(getResources().getColor(R.color.shadowed));
                            v.performClick();
                            break;
                        default:
                            break;
                    }
                    return true;
                });
            }
        }


        Enums.ButtonsPlace buttonsPlace = Enums.ButtonsPlace.convert(
                DataReader.GetInt(DataReader.BUTTONS_PLACE, this)
        );

        Enums.LayoutState layoutState = Enums.LayoutState.convert(
                DataReader.GetInt(DataReader.LAYOUT_STATE, this)
        );

        if (buttonsPlace == Enums.ButtonsPlace.LEFT)
            alterActionSides();
        if (layoutState == Enums.LayoutState._123)
            alterLayout();
    }

    //меняет раскладку с 789 на 123
    void alterLayout() {
        switchPlaces(findViewById(R.id.Layout_7),
                findViewById(R.id.Layout_1));
        switchPlaces(findViewById(R.id.Layout_8),
                findViewById(R.id.Layout_2));
        switchPlaces(findViewById(R.id.Layout_9),
                findViewById(R.id.Layout_3));
    }

    //меняет местами два объекта
    void switchPlaces(View b1, View b2) {
        ViewGroup.LayoutParams temp = b1.getLayoutParams();
        b1.setLayoutParams(b2.getLayoutParams());
        b2.setLayoutParams(temp);
    }

    //перемещает кнопки-значки в левую сторону экрана
    void alterActionSides() {
        View imagebutton = findViewById(R.id.Layout_Del);
        switchPlaces(findViewById(R.id.Layout_9), imagebutton);
        switchPlaces(findViewById(R.id.Layout_8), imagebutton);
        switchPlaces(findViewById(R.id.Layout_7), imagebutton);

        imagebutton = findViewById(R.id.Layout_skip);
        switchPlaces(findViewById(R.id.Layout_6), imagebutton);
        switchPlaces(findViewById(R.id.Layout_5), imagebutton);
        switchPlaces(findViewById(R.id.Layout_4), imagebutton);

        imagebutton = findViewById(R.id.Layout_help);
        switchPlaces(findViewById(R.id.Layout_3), imagebutton);
        switchPlaces(findViewById(R.id.Layout_2), imagebutton);
        switchPlaces(findViewById(R.id.Layout_1), imagebutton);

        imagebutton = findViewById(R.id.Layout_check);
        switchPlaces(findViewById(R.id.Layout_sym), imagebutton);
        switchPlaces(findViewById(R.id.Layout_0), imagebutton);
        switchPlaces(findViewById(R.id.Layout_empty), imagebutton);
    }

    @Override
    public void onBackPressed() {
        crossClick(findViewById(R.id.cross));
    }

    //сохраняем информацию о данной попытке и обновляем информацию раунда
    private void saveTaskStatistic(boolean finished) {
        task.makeUserAnswer(answer, String.valueOf((System.currentTimeMillis() - prevAnswerTime) / 1000));
        prevAnswerTime = System.currentTimeMillis();

        if (finished) {
            task.setTimeTaken((System.currentTimeMillis() - prevTaskTime) / 1000);
            currentTour.addTask(task);
            prevTaskTime = prevAnswerTime;
        } else {
            currentTour.addTask(task);
            task = Task.makeTask(task);
        }

        //обновляем реальную продолжительность раунда
        currentTour.setTourTime((Calendar.getInstance().getTimeInMillis() - currentTour.getTourDateTime()) / 1000);
    }

    //нажатие на кнопку "ОК", проверяем правильность ответа и заносим в статистику
    public void okButtonClick(View view) {
        resetMode();
        if (answer.equals(""))
            return;

        resetDissapearing();
        updateTimers();

        if (answerShown) {
            answerI = "?";
            answerT = "";
            answerB = "";
            answerF = "";
            answer = "?";
            answerShown = false;
        }

        if (answer.equals(task.getAnswer())) {
            updateProgressIcons(getString(R.string.star));
            startNewTask();
        } else {
            saveTaskStatistic(false);
            if (!answer.equals("?")) {
                saveCorrectionWork();
                updateWrongAnswers(answer);
                updateProgressIcons(getString(R.string.dot));
                answerWasShown = false;
            }
            answerI = "";
            answerT = "";
            answerB = "";
            answerF = "";
            answer = "";
            textViewUpdate();
        }
    }

    //выводит новый неправильный ответ к остальным
    private void updateWrongAnswers(String answer) {
        TextView wrongAnswers = findViewById(R.id.wrongAnswers);
        StringBuilder text = new StringBuilder();
        String line = wrongAnswers.getText().toString();
        if (!line.isEmpty()) {
            String[] answers = line.split(", ");
            for (String s : answers)
                text.append(String.format(Locale.getDefault(), "<strike>%s</strike>, ", s));
        }
        text.append(String.format(Locale.getDefault(), "<strike>%s</strike>", answer));
        wrongAnswers.setText(Html.fromHtml(text.toString()));
    }

    //пропуск задания
    public void skipTask(View view) {
        resetDissapearing();
        updateTimers();
        if (answerShown) {
            answer = "?";
        } else {
            answer = "\u2026";
            updateProgressIcons(getString(R.string.arrow));
        }
        startNewTask();
    }

    //запуск нового задания
    private void startNewTask() {
        saveTaskStatistic(true);
        task = taskQueue.newTask();


        correctionwork = null;
        answerShown = false;
        answerWasShown = false;

        answer = "";
        answerI = "";
        answerT = "";
        answerB = "";
        answerF = "";
        resetMode();

        TextView wrongAnswers = findViewById(R.id.wrongAnswers);
        wrongAnswers.setText("");

        updateTimers();

        if ((Calendar.getInstance().getTimeInMillis() - tourStartTime) / 1000 >= tourLenght)
            endRound();
        else
            textViewUpdate();
    }

    //добавляем иконку в зависимости от статуса задания
    private void updateProgressIcons(String icon) {
        TextView pi = findViewById(R.id.progressIcons);
        String text = pi.getText().toString();
        text += icon;
        pi.setText(text);

        shown++;
        if (icon.equals(getString(R.string.star)))
            solved++;

        TextView statistics = findViewById(R.id.statistics);
        statistics.setText(String.format(Locale.getDefault(), "Решено %d/%d (%d%%)",
                solved, shown - 1, solved * 100 / (shown - 1)));
    }

    //завершение раунда
    private void endRound() {
        if (answerShown) {
            answer = "?";
            saveTaskStatistic(true);
        }
        StatisticMaker.saveTour(currentTour, context);
        Log.d("debuggggggggggggggggg", "2222222");
        taskQueue.save();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)//, R.style.AlertDialogTheme)
                .setTitle("Раунд завершён")
                .setMessage(String.format(Locale.getDefault(), "Решено заданий: %d/%d (%d%%)",
                        currentTour.getRightTasks(),
                        currentTour.getTotalTasks(),
                        100 * currentTour.getRightTasks() / currentTour.getTotalTasks()))
                .setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_endround, null);

        TextView donate = view.findViewById(R.id.donate);
        SpannableString ss = new SpannableString(getString(R.string.donatetext));
        ss.setSpan(new URLSpan(getString(R.string.donatelink)), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        donate.setText(ss);

        builder.setView(view)
                .setNeutralButton("Ещё раунд", (dialog, which) -> {
                    finish();
                    startActivity(getIntent());
                })
                .setNegativeButton("Подробнее", (dialog, which) -> {
                    finish();
                    int tourCount = StatisticMaker.getTourCount(context);
                    Intent i = new Intent(context, StatTourActivity.class);
                    i.putExtra("Tour", (Integer) (tourCount - 1));
                    startActivity(i);
                })
                .setPositiveButton("В начало", (dialog, which) -> finish());

        AlertDialog dialog = builder.create();
        dialog.setOnKeyListener((arg0, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.getButton(dialog.BUTTON_POSITIVE).callOnClick();
            }
            return true;
        });
        dialog.show();
        Utils.FixDialog(dialog, context); // почему-то нужно для планшетов
    }

    public void goToWeb(View view) {
        Intent intent = new Intent(this, WebActivity.class);
        startActivity(intent);
        finish();
    }

    //досрочное завершение раунда по нажатию выхода
    public void crossClick(View view) {
        if (currentTour.getTotalTasks() == 0 && task.getUserAnswer().length() == 0 && !answerShown) {
            finish();
            return;
        }

        AlertDialog dialog =
                new AlertDialog.Builder(this)//, R.style.AlertDialogTheme)
                        .setTitle("Досрочное завершение раунда")
                        .setMessage("Сохранить результаты?")
                        .setNeutralButton("Отмена", (dialog1, which) -> {
                        })
                        .setNegativeButton("Нет", (dialog12, which) -> {
                            taskQueue.save();
                            finish();
                        })
                        .setPositiveButton("Да", (dialog13, which) -> endRound()).show();
        Utils.FixDialog(dialog, context);// почему-то нужно для планшетов

    }

    //обновляем поля вывода выражения
    private void textViewUpdate() {
        TextView expressionTV = findViewById(R.id.expTextView);

        if (tasktype.equals("fractions")) {
            answerF = Fraction.makeFraction(answerT, answerB);
            if (answerF.isEmpty() && mode == Mode.top)
                answerF = " ⁄";
            answer = answerI + answerF;
        }

        if (showTask)
            expressionTV.setText(task.getExpression() + " = " + answer);
        else
            expressionTV.setText(getString(R.string.threedots) + " = " + answer);
    }

    //нажатие на цифровую кнопку
    public void numberPress(View view) {
        if (answerShown && answer.length() > 0) {
            return;
        }
        String symbol = view.getTag().toString();

        if (answer.equals("0") && !symbol.equals(",")) {
            answer = "";
            textViewUpdate();
        } else if (symbol.equals(",") && (answer.contains(",") || answer.isEmpty()))
            return;

        if (symbol.equals("/")) {
            switchMode(true);
            textViewUpdate();
            return;
        }

        if (!tasktype.equals("fractions"))
            answer += symbol;
        else
            switch (mode) {
                case integer:
                    if (!(symbol.equals("0") && answerI.isEmpty()))
                        answerI += symbol;
                    break;
                case top:
                    if (!(symbol.equals("0") && answerT.isEmpty()))
                        answerT += symbol;
                    break;
                case bottom:
                    if (!(symbol.equals("0") && answerB.isEmpty()))
                        answerB += symbol;
                    break;
            }

        textViewUpdate();
    }

    void switchMode(boolean forward) {
        if (forward)
            switch (mode) {
                case integer:
                    mode = Mode.top;
                    break;
                case top:
                    if (!answerT.isEmpty())
                        mode = Mode.bottom;
                    break;
                case bottom:
                default:
                    break;
            }
        else
            switch (mode) {
                case top:
                    mode = Mode.integer;
                    break;
                case bottom:
                    mode = Mode.top;
                    break;
                case integer:
                default:
                    break;
            }
        setModeSym();
    }

    private void setModeSym() {
        ((Button) findViewById(R.id.Button_sym)).
                setText(fractionSymsSpan[mode.ordinal()],
                        TextView.BufferType.SPANNABLE);
    }

    void resetMode() {
        if (tasktype.equals("fractions")) {
            mode = Mode.integer;
            setModeSym();
        }
    }

    //удаление одного символа
    public void charDelete(View view) {
        if (!answerShown) {
            if (answer.length() > 0 || tasktype.equals("fractions")) {//(answerI + answerT + answerB).length() > 0) {
                if (!tasktype.equals("fractions"))
                    answer = answer.substring(0, answer.length() - 1);
                else
                    switch (mode) {
                        case bottom:
                            if (!answerB.isEmpty()) {
                                answerB = answerB.substring(0, answerB.length() - 1);
                                break;
                            } else
                                switchMode(false);
                        case top:
                            if (!answerT.isEmpty()) {
                                answerT = answerT.substring(0, answerT.length() - 1);
                                break;
                            } else
                                switchMode(false);
                        case integer:
                        default:
                            if (!answerI.isEmpty())
                                answerI = answerI.substring(0, answerI.length() - 1);
                            break;
                    }
                textViewUpdate();
            }
        }
    }

    //показать ответ
    public void showAnswer(View view) {
        resetMode();
        answer = task.getAnswer();
        answerI = task.getAnswer();
        answerF = "";

        answerShown = true;
        if (!answerWasShown)
            updateProgressIcons("?");
        answerWasShown = true;
        textViewUpdate();

        saveCorrectionWork();
    }

    void saveCorrectionWork() {
        if (correctionwork == null) {
            correctionwork = task;
            taskQueue.Add(task);
        }
    }

    //таймер для исчезновения задания
    private final Runnable disapTask = () -> {
        showTask = false;
        textViewUpdate();
        TextView pressToShowTaskTV = findViewById(R.id.pressToShowTaskTV);
        //pressToShowTaskTV.setText("Нажмите, чтобы показать задание");
        pressToShowTaskTV.setVisibility(View.VISIBLE);
        TextView pi = findViewById(R.id.progressIcons);
        pi.setVisibility(View.INVISIBLE);
    };

    //Таймер перезапускается в 4-х случаях:
    //1) При первичной инициализации, 2) при нажатии на ОК, 3) при нажатии на пропуск задания, 4) при нажатии на showTask.
    //Не перезапускается при цифровых кнопках, DEL, показывании ответа
    //При перезапуске таймера необходимо показать задание. Это нужно делать перед textViewUpdate.
    private void resetDissapearing() {
        showTask = true;
        TextView pressToShowTaskTV = findViewById(R.id.pressToShowTaskTV);
        //pressToShowTaskTV.setText("Нажмите, чтобы показать задание");
        pressToShowTaskTV.setVisibility(View.INVISIBLE);
        TextView pi = findViewById(R.id.progressIcons);
        pi.setVisibility(View.VISIBLE);

        taskDisapHandler.removeCallbacks(disapTask);
        if (disapTime > -1) {
            taskDisapHandler.postDelayed(disapTask, (long) (disapTime * 1000));
        }
    }

    //просмотр задания, если оно исчезло
    public void showTask(View view) {
        resetDissapearing();
        TextView pressToShowTaskTV = findViewById(R.id.pressToShowTaskTV);
        //pressToShowTaskTV.setText("");
        pressToShowTaskTV.setVisibility(View.INVISIBLE);
        textViewUpdate();
    }

    //при выходе из активности необходимо остановить все таймеры
    @Override
    protected void onDestroy() {
        taskDisapHandler.removeCallbacks(disapTask);
        if (progressthread != null)
            progressthread.interrupt();
        if (timerthread != null)
            timerthread.interrupt();
        super.onDestroy();
    }
}