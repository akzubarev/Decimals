package com.education4all.decimals;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;

import java.lang.Runnable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.gridlayout.widget.GridLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.education4all.decimals.MathCoachAlg.DataReader;
import com.education4all.decimals.MathCoachAlg.StatisticMaker;


import com.education4all.decimals.MathCoachAlg.Tour;
import com.education4all.decimals.MathCoachAlg.Task;


public class TaskActivity extends AppCompatActivity {
    private Tour currentTour = new Tour(); // элемент класса Tour - текущий тур
    private Handler roundTimeHandler = new Handler(); // хэндлер для времени раунда
    private Handler progressBarHandler = new Handler(); // хэндлер для прогресс бара
    private Handler taskDisapHandler = new Handler(); // хэндлер для времени исчезновения
    private Task newTask; // текущее задание
    private String answer; // текущий ответ
    private float RoundTime; // время раунда
    private ProgressBar G_progressBar; // прогресс бар
    private int progressStatus = 0; //позиция прогресс бара
    private Context context = this; // переменная контекста, нужна чтобы передавть её в другие классы
    private boolean answerShown = false; // показан ли ответ
    private boolean showTask = true; // показано ли задание
    private float disapTime; // время изсчезновения задания
    private int[][] allowedTasks; // массив разрешённых заданий
    private ListView mDrawerList; // шторка внутренности
    private DrawerLayout mDrawerLayout; // сама шторка
    private ArrayAdapter<String> mAdapter; // адаптер для фоматирования строк
    long prevTaskTime;
    long tourStartTime; //время начала раунда
    long millis; //время раунда в миллисекундах
    int seconds; //время раунда в секундах
    int count; //текущее время раунда
    int solved = 0; //сколько заданий решено
    int shown = 1; //сколько заданий показано


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);

        //делаем текст зачеркнутым
//        TextView wrongAnswers = findViewById(R.id.wrongAnswers);
//        wrongAnswers.setPaintFlags(wrongAnswers.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        //заполняем GridLayout
        GridLayout gl = (GridLayout) findViewById(R.id.buttonsLayout);
        ViewTreeObserver vto = gl.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                GridLayout gl = (GridLayout) findViewById(R.id.buttonsLayout);
                fillView(gl);
                ViewTreeObserver obs = gl.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
            }
        });

        //обнуляем переменные и инициализируем элементы layout
        Intent intent = getIntent();
        newTask = new Task();
        allowedTasks = DataReader.readAllowedTasks(this);
        if (!newTask.areTasks(allowedTasks)) {
            finish();
            startActivity(new Intent(context, SettingsMainActivity.class).putExtra("FromTask", true));
        } else {
            tourStartTime = Calendar.getInstance().getTimeInMillis();
            prevTaskTime = tourStartTime;
            currentTour.totalTasks = 0;
            currentTour.rightTasks = 0;
            answer = new String();

            G_progressBar = (ProgressBar) findViewById(R.id.taskProgress);
            G_progressBar.setProgress(0);
            final int timerstate = DataReader.GetTimerState(this);//0 - continous 1 - discrete 2 - invisible
            G_progressBar.setVisibility(timerstate == 2 ? View.INVISIBLE : View.VISIBLE);
            RoundTime = DataReader.GetRoundTime(this);
            millis = (long) (RoundTime * 1000 * 60);
            final Context l_context = this;
            new Thread(() -> {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    if (timerstate == 0)
                        progressBarHandler.post(() -> G_progressBar.setProgress(progressStatus));
                    try {
                        Thread.sleep((long) (RoundTime * 600));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


            TextView timerText = findViewById(R.id.timertext);
            timerText.setVisibility(timerstate == 2 ? View.INVISIBLE : View.VISIBLE);
            seconds = (int) RoundTime * 60;

            timerText.setText(timeString(count, seconds));

            new Thread(() -> {
                while (count < seconds) {
                    count += 1;
                    if (timerstate == 0)
                        timerText.setText(timeString(count, seconds));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            disapTime = DataReader.GetDisapRoundTime(this);
            //roundTimeHandler.postDelayed(endRound, millis);
            showTaskSetTrueAndRestartDisappearTimer();
            newTask.generate(allowedTasks);
            textViewUpdate();

            findViewById(R.id.But_Del).setOnLongClickListener(v -> {
                answer = "";
                textViewUpdate();
                return true;
            });

            GridLayout parent = findViewById(R.id.buttonsLayout);
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child.getClass() == AppCompatButton.class) {
                    final String childtag = child.getTag().toString();
                    child.setOnLongClickListener(v -> {
//                        int resID = getResources().getIdentifier(!childtag.equals(",") ? "line" + childtag : "linecomma",
//                                "id", "com.education4all.decimals");
//                        View line = findViewById(resID);
//                        line.setBackgroundColor(getResources().getColor(R.color.main));
                        numberPress(child);
                        return true;
                    });

                    child.setOnTouchListener((v, event) -> {
                        int resID = getResources().getIdentifier(!childtag.equals(",") ? "line" + childtag : "linecomma",
                                "id", "com.education4all.decimals");
                        View line = findViewById(resID);
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
        }
    }

    private String timeString(int count, int seconds) {
      return String.format("%s/%s", DateUtils.formatElapsedTime(count), DateUtils.formatElapsedTime(seconds));
    }

    //делаем все кнопки одинакового размера внутри GridLayout
    public void fillView(GridLayout parent) {
        //Button child;
        View child;
        //parent.setBackgroundColor(Color.DKGRAY);
        for (int i = 0; i < parent.getChildCount(); i++) {
            // child = (Button)parent.getChildAt(i);
            child = parent.getChildAt(i);
            if (child.getClass() != View.class) {
                GridLayout.LayoutParams params = (GridLayout.LayoutParams) child.getLayoutParams();
                int margin = -8;
                if (child.getClass() == AppCompatButton.class)//|| child.getClass() == ImageButton.class)
                    params.setMargins(margin, margin, margin, margin);

                params.width = (parent.getWidth() / parent.getColumnCount()) - params.rightMargin - params.leftMargin;
                params.height = (parent.getHeight() / parent.getRowCount()) - params.bottomMargin - params.topMargin;
                //child.setBackgroundColor(Color.DKGRAY);
//            if (child.getClass() == AppCompatImageButton.class) {
//                params.width *= 0.5 * 0.7;
//                params.height *= 0.5 * 0.7;
//            }
                child.setLayoutParams(params);
                //child.setBackgroundColor(parent.getDrawingCacheBackgroundColor());
            }
        }

        if (DataReader.GetButtonsPlace(this) == 1)
            alterButtons(parent);
        if (DataReader.GetLayoutState(this) == 0)
            alterLayout(parent);
    }

    void alterLayout(GridLayout parent) {
        Button b1 = findViewById(R.id.But_7);
        Button b2 = findViewById(R.id.But_1);
        switchPlaces(b1, b2);
        b1 = findViewById(R.id.But_8);
        b2 = findViewById(R.id.But_2);
        switchPlaces(b1, b2);
        b1 = findViewById(R.id.But_9);
        b2 = findViewById(R.id.But_3);
        switchPlaces(b1, b2);


        View v1 = findViewById(R.id.line7);
        View v2 = findViewById(R.id.line1);
        switchPlaces(v1, v2);

        v1 = findViewById(R.id.line8);
        v2 = findViewById(R.id.line2);
        switchPlaces(v1, v2);

        v1 = findViewById(R.id.line9);
        v2 = findViewById(R.id.line3);
        switchPlaces(v1, v2);

    }

    void switchPlaces(View b1, View b2) {
        ViewGroup.LayoutParams temp = b1.getLayoutParams();
        b1.setLayoutParams(b2.getLayoutParams());
        b2.setLayoutParams(temp);
    }

    void alterButtons(GridLayout parent) {
        View b1 = findViewById(R.id.But_7);
        View b2 = findViewById(R.id.But_8);
        View b3 = findViewById(R.id.But_9);
        View b4 = findViewById(R.id.But_Del);
        switchPlaces(b1, b4);
        switchPlaces(b2, b1);
        switchPlaces(b3, b2);

        b1 = findViewById(R.id.But_4);
        b2 = findViewById(R.id.But_5);
        b3 = findViewById(R.id.But_6);
        b4 = findViewById(R.id.But_Skip);
        switchPlaces(b1, b4);
        switchPlaces(b2, b1);
        switchPlaces(b3, b2);

        b1 = findViewById(R.id.But_1);
        b2 = findViewById(R.id.But_2);
        b3 = findViewById(R.id.But_3);
        b4 = findViewById(R.id.But_Help);
        switchPlaces(b1, b4);
        switchPlaces(b2, b1);
        switchPlaces(b3, b2);

        b1 = findViewById(R.id.But_empty);
        b2 = findViewById(R.id.But_0);
        b3 = findViewById(R.id.But_comma);
        b4 = findViewById(R.id.But_Check);
        switchPlaces(b1, b4);
        switchPlaces(b2, b1);
        switchPlaces(b3, b2);

        TextView text = findViewById(R.id.deletetext);
        GridLayout.LayoutParams params = (GridLayout.LayoutParams) text.getLayoutParams();
        params.columnSpec = GridLayout.spec(0);
        text.setLayoutParams(params);

        text = findViewById(R.id.skiptext);
        params = (GridLayout.LayoutParams) text.getLayoutParams();
        params.columnSpec = GridLayout.spec(0);
        text.setLayoutParams(params);

        text = findViewById(R.id.helptext);
        params = (GridLayout.LayoutParams) text.getLayoutParams();
        params.columnSpec = GridLayout.spec(0);
        text.setLayoutParams(params);

        text = findViewById(R.id.checktext);
        params = (GridLayout.LayoutParams) text.getLayoutParams();
        params.columnSpec = GridLayout.spec(0);
        text.setLayoutParams(params);

        b1 = findViewById(R.id.line7);
        b2 = findViewById(R.id.line8);
        b3 = findViewById(R.id.line9);
        b4 = findViewById(R.id.linedel);
        switchPlaces(b1, b4);
        switchPlaces(b2, b1);
        switchPlaces(b3, b2);

        b1 = findViewById(R.id.line4);
        b2 = findViewById(R.id.line5);
        b3 = findViewById(R.id.line6);
        b4 = findViewById(R.id.lineskip);
        switchPlaces(b1, b4);
        switchPlaces(b2, b1);
        switchPlaces(b3, b2);

        b1 = findViewById(R.id.line1);
        b2 = findViewById(R.id.line2);
        b3 = findViewById(R.id.line3);
        b4 = findViewById(R.id.linehelp);
        switchPlaces(b1, b4);
        switchPlaces(b2, b1);
        switchPlaces(b3, b2);

        b1 = findViewById(R.id.lineempty);
        b2 = findViewById(R.id.line0);
        b3 = findViewById(R.id.linecomma);
        b4 = findViewById(R.id.linecheck);
        switchPlaces(b1, b4);
        switchPlaces(b2, b1);
        switchPlaces(b3, b2);

    }

    @Override
    public void onBackPressed() {
        crossClick(findViewById(R.id.cross));
    }

    //сохраняем информацию о данной попытке и обновляем информацию раунда
    private void saveTaskStatistic() {
        if (answer.equals(newTask.answer)) {
            ++currentTour.rightTasks; // увеличиваем счетчик правильных заданий, если ответ правильный
        }
//        newTask.userAnswer = answer + ":" + (System.currentTimeMillis() - prevTaskTime) / 1000;
        newTask.userAnswer += answer + ":" + (System.currentTimeMillis() - prevTaskTime) / 1000 + '|';
        prevTaskTime = System.currentTimeMillis();
        newTask.timeTaken = (System.currentTimeMillis() - newTask.taskTime) / 1000; //??? вычисляем время, потраченное пользователем на ПРАВИЛЬНОЕ решение текущего задания
        currentTour.tourTasks.add(newTask); //сохраняем информацию о текущем задании
        currentTour.tourTime = (Calendar.getInstance().getTimeInMillis() - currentTour.tourDateTime) / 1000; //обновляем реальную продолжительность раунда
        ++currentTour.totalTasks; //увеличиваем счетчик количества заданий в раунде
    }

    //нажатие на кнопку "ОК", проверяем правильность ответа и заносим в статистику
    public void okButtonClick(View view) {
        showTaskSetTrueAndRestartDisappearTimer();
        if (answer.equals("")) return;
        if (answerShown) {
            answer = "?";
            answerShown = false;
            textViewUpdate();
        }
        saveTaskStatistic();

        if (answer.equals(newTask.answer)) {
            updateProgressIcons(getString(R.string.star));
            solved++;
            startNewTask();
        } else {
            if (!answer.equals("?")) {
                updateWrongAnswers(answer);
                updateProgressIcons(getString(R.string.dot));
            }
            answer = "";
            textViewUpdate();
        }
    }

    private void updateWrongAnswers(String answer) {
        TextView wrongAnswers = findViewById(R.id.wrongAnswers);
        String text = "";
        String line = wrongAnswers.getText().toString();
        if (!line.isEmpty()) {
            String[] answers = line.split(", ");
            for (String s : answers)
                text += String.format("<strike>%s</strike>, ", s);
        }
        text += String.format("<strike>%s</strike>", answer);
        wrongAnswers.setText(Html.fromHtml(text));
    }

    //пропуск задания
    public void skipTask(View view) {
        showTaskSetTrueAndRestartDisappearTimer();
        if (answerShown) {
            answer = "?";
        } else {
            answer = "\u2026";
        }
        saveTaskStatistic();
        updateProgressIcons(getString(R.string.arrow));
        startNewTask();
    }

    //запуск нового задания
    private void startNewTask() {
        newTask = new Task();
        answerShown = false;
        newTask.generate(allowedTasks);
        answer = "";

        TextView wrongAnswers = findViewById(R.id.wrongAnswers);
        wrongAnswers.setText("");

        ((TextView) findViewById(R.id.timertext)).setText(timeString(count, seconds));

        G_progressBar.setProgress(progressStatus);


        shown++;
        TextView statistics = findViewById(R.id.statistics);
        statistics.setText(String.format("Решено %d из %d (%d%%)", solved, shown - 1, solved * 100 / (shown - 1)));


        if (Calendar.getInstance().getTimeInMillis() - tourStartTime >= millis) {
            endRound();
        } else {
            textViewUpdate();
        }

    }

    //добавляем иконку в зависимости от статуса задания
    private void updateProgressIcons(String icon) {
        TextView pi = (TextView) findViewById(R.id.progressIcons);
        String text = pi.getText().toString();
        // if (text.length() > 20)
        //     text = text.substring(1);
        text += icon;
        pi.setText(text);
    }

    //завершение раунда
    private void endRound() {
//        saveTaskStatistic(); // Здесь сохранять статистику задания не нужно!
        if (answerShown) {
            answer = "?";
            saveTaskStatistic();
        }
        StatisticMaker.saveTour(currentTour, context);

        AlertDialog dialog = new AlertDialog.Builder(TaskActivity.this, R.style.AlertDialogTheme)
                .setTitle("Раунд завершён")
                .setMessage("Решено заданий: " + Integer.toString(currentTour.rightTasks) + " из " + Integer.toString(currentTour.totalTasks))
                .setCancelable(false)
                .setNeutralButton("Ещё раунд", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                })
                .setNegativeButton("Подробнее", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        int tourCount = StatisticMaker.getTourCount(context);
                        Intent i = new Intent(context, StatTourActivity.class);
                        i.putExtra("Tour", (Integer) (tourCount - 1));
                        startActivity(i);
                    }
                })
                .setPositiveButton("В начало", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
        CommonOperations.FixDialog(dialog, context);
    }

    //досрочное завершение раунда по нажатию выхода
    public void crossClick(View view) {
        if (currentTour.totalTasks == 0 && !answerShown) {
            finish();
        } else {
            AlertDialog dialog =
                    new AlertDialog.Builder(TaskActivity.this, R.style.AlertDialogTheme)
                            .setTitle("Досрочное завершение раунда")
                            .setMessage("Сохранить результаты?")
                            .setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
//                        saveTaskStatistic(); //Текущее задание не записываем!
//                            StatisticMaker.saveTour(currentTour, context); // Результаты тура тут сохранять не нужно, они сохранятся при завершении раунда.
                                    endRound();
                                }
                            }).show();
            CommonOperations.FixDialog(dialog, context);
        }
    }

    //обновляем поля вывода выражения
    private void textViewUpdate() {
        TextView expressionTV = (TextView) findViewById(R.id.expTextView);
        if (showTask) {
            expressionTV.setText(newTask.expression + " = " + answer);
        } else {
            expressionTV.setText("\u2026 = " + answer);
//            pressToShowTaskTV.setText("Нажмите, чтобы показать задание");
//            int x = expressionTV.getLeft();
//            int y = expressionTV.getBottom() - expressionTV.getHeight() /3;
//            Toast toast = Toast.makeText(this, "Нажмите, чтобы показать задание", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.TOP, x, y);
//            toast.show();;
        }
    }

    //нажатие на цифровую кнопку
    public void numberPress(View view) {
        if (answerShown && answer.length() > 0) {
            return;
        }
        String symbol = view.getTag().toString();

//        int resID = getResources().getIdentifier(!symbol.equals(",") ? "line" + symbol : "linecomma", "id", "com.education4all.decimals");
//        View line = findViewById(resID);
//        line.setBackgroundColor(getResources().getColor(R.color.main));
//        final Handler handler = new Handler();
//        handler.postDelayed(() -> line.setBackgroundColor(getResources().getColor(R.color.shadowed)), 100);


        if (answer.equals("0") && !symbol.equals(",")) {
            answer = "";
            textViewUpdate();
        } else if ((answer.isEmpty() && symbol.equals(",")) ||
                (answer.contains(",") && symbol.equals(",")))
            return;
        answer = answer + symbol;
        textViewUpdate();
    }

    //удаление одного символа
    public void charDelete(View view) {
        if (!answerShown) {
            if (answer.length() > 0) {
                answer = answer.substring(0, answer.length() - 1);
            }
            textViewUpdate();
        }
    }

    //показать ответ
    public void showAnswer(View view) {
        answer = newTask.answer;
        answerShown = true;
        updateProgressIcons("?");
        textViewUpdate();
    }

    //таймер для исчезновения задания
    private Runnable disapTask = new Runnable() {
        public void run() {
            showTask = false;
            textViewUpdate();
            TextView pressToShowTaskTV = (TextView) findViewById(R.id.pressToShowTaskTV);
            //pressToShowTaskTV.setText("Нажмите, чтобы показать задание");
            pressToShowTaskTV.setVisibility(View.VISIBLE);
            TextView pi = (TextView) findViewById(R.id.progressIcons);
            pi.setVisibility(View.INVISIBLE);
        }
    };

    //Таймер перезапускается в 4-х случаях:
    //1) При первичной инициализации, 2) при нажатии на ОК, 3) при нажатии на пропуск задания, 4) при нажатии на showTask.
    //Не перезапускается при цифровых кнопках, DEL, показывании ответа
    //При перезапуске таймера необходимо показать задание. Это нужно делать перед textViewUpdate.
    private void showTaskSetTrueAndRestartDisappearTimer() {
        showTask = true;
        TextView pressToShowTaskTV = (TextView) findViewById(R.id.pressToShowTaskTV);
        //pressToShowTaskTV.setText("Нажмите, чтобы показать задание");
        pressToShowTaskTV.setVisibility(View.INVISIBLE);
        TextView pi = (TextView) findViewById(R.id.progressIcons);
        pi.setVisibility(View.VISIBLE);

        taskDisapHandler.removeCallbacks(disapTask);
        if (disapTime > -1) {
            taskDisapHandler.postDelayed(disapTask, (long) (disapTime * 1000));
        }
    }

    //просмотр задания, если оно исчезло
    public void showTask(View view) {
        showTaskSetTrueAndRestartDisappearTimer();
        TextView pressToShowTaskTV = (TextView) findViewById(R.id.pressToShowTaskTV);
        //pressToShowTaskTV.setText("");
        pressToShowTaskTV.setVisibility(View.INVISIBLE);
        textViewUpdate();
    }

    //при выходе из активности необходимо остановить все таймеры
    @Override
    protected void onDestroy() {
        taskDisapHandler.removeCallbacks(disapTask);
        super.onDestroy();
    }
}