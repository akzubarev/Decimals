package com.education4all.activities;

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
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.gridlayout.widget.GridLayout;

import com.education4all.BuildConfig;
import com.education4all.R;
import com.education4all.utils.Enums;
import com.education4all.mathCoachAlg.DataReader;
import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tours.TaskQueue;
import com.education4all.mathCoachAlg.tasks.Fraction;
import com.education4all.mathCoachAlg.tasks.Task;
import com.education4all.mathCoachAlg.tours.Tour;
import com.education4all.utils.Utils;

import java.util.Calendar;

public class TaskActivity extends AppCompatActivity {
    final String tasktype = BuildConfig.FLAVOR; //тип заданий
    private final Tour currentTour = new Tour(); // элемент класса Tour - текущий тур
    private Handler roundTimeHandler = new Handler(); // хэндлер для времени раунда
    private final Handler progressBarHandler = new Handler(); // хэндлер для прогресс бара
    private final Handler taskDisapHandler = new Handler(); // хэндлер для времени исчезновения
    private Task task; // текущее задание
    private String answer = "", // текущий ответ
            answerI = "", // текущая целая часть ответа
            answerT = "", // текущий числитель ответа
            answerB = "", // текущий знаменатель ответа
            answerF = ""; // текущая дробь ответа
    //  SpannableString answerF = new SpannableString(""); // текущая дробь ответа
    private float RoundTime; // время раунда
    private TextView timerText; //текстовый таймер
    private ProgressBar G_progressBar; // прогресс бар
    private int progressStatus = 0; //позиция прогресс бара
    private final Context context = this; // переменная контекста, нужна чтобы передавть её в другие классы
    private boolean answerShown = false; // показан ли ответ
    private boolean showTask = true; // показано ли задание
    private float disapTime; // время изсчезновения задания
    private int[][] allowedTasks; // массив разрешённых заданий
    private ListView mDrawerList; // шторка внутренности
    private DrawerLayout mDrawerLayout; // сама шторка
    private ArrayAdapter<String> mAdapter; // адаптер для фоматирования строк
    long prevTaskTime;
    long prevAnswerTime;
    long tourStartTime; //время начала раунда
    long millis; //время раунда в миллисекундах
    int seconds; //время раунда в секундах
    int count; //текущее время раунда
    int solved = 0; //сколько заданий решено
    int shown = 1; //сколько заданий показано
    boolean answerWasShown = false; //был ли уже показан ответ
    final String id = BuildConfig.APPLICATION_ID;
    Mode mode = Mode.integer;
    Task correctionwork = null;
    TaskQueue taskQueue;

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

        int size = (int) getResources().getDimension(R.dimen.dimen0) / 3;
        for (int i = 0; i < fractionSyms.length; i++) {

            fractionSymsSpan[i] = SpannableString.valueOf(fractionSyms[i]);
            fractionSymsSpan[i].setSpan(new SuperscriptSpan(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            fractionSymsSpan[i].setSpan(new AbsoluteSizeSpan(size), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // fractionSymsSpan[i].setSpan(new SubscriptSpan(), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            fractionSymsSpan[i].setSpan(new AbsoluteSizeSpan(size), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        setContentView(R.layout.task);


        //заполняем GridLayout
        GridLayout gl = findViewById(R.id.buttonsLayout);
        ViewTreeObserver vto = gl.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                GridLayout gl = findViewById(R.id.buttonsLayout);
                fillView(gl);
                ViewTreeObserver obs = gl.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
            }
        });

        //обнуляем переменные и инициализируем элементы layout
        allowedTasks = DataReader.readAllowedTasks(this);
        if (!Task.areTasks(allowedTasks)) {
            finish();
            startActivity(
                    new Intent(context, SettingsMainActivity.class)
                            .putExtra("FromTask", true)
            );
        } else {
            tourStartTime = System.currentTimeMillis();
            prevTaskTime = tourStartTime;
            prevAnswerTime = tourStartTime;
            answer = "";

            final Enums.TimerState timerstate = Enums.TimerState.convert(
                    DataReader.GetInt(DataReader.TIMER_STATE, this));


            G_progressBar = findViewById(R.id.taskProgress);
            G_progressBar.setProgress(0);
            G_progressBar.setVisibility(timerstate == Enums.TimerState.INVISIBlE ?
                    View.INVISIBLE : View.VISIBLE);
            RoundTime = DataReader.GetInt(DataReader.ROUND_TIME, this);
            millis = (long) (RoundTime * 1000 * 60);
            new Thread(() -> {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    if (timerstate == Enums.TimerState.CONTINIOUS)
                        progressBarHandler.post(() -> G_progressBar.setProgress(progressStatus));
                    try {
                        Thread.sleep((long) (RoundTime * 600));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


            timerText = findViewById(R.id.timertext);
            timerText.setVisibility(timerstate == Enums.TimerState.INVISIBlE ?
                    View.INVISIBLE : View.VISIBLE);

            seconds = (int) RoundTime * 60;
            timerText.setText(timeString(count, seconds));

            new Thread(() -> {
                while (count < seconds) {
                    count += 1;
                    if (timerstate == Enums.TimerState.CONTINIOUS)
                        timerText.setText(timeString(count, seconds));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            disapTime = DataReader.GetInt(DataReader.DISAP_ROUND_TIME, this);
            //roundTimeHandler.postDelayed(endRound, millis);
            showTaskSetTrueAndRestartDisappearTimer();

            taskQueue = new TaskQueue(allowedTasks, context);
            Task.setAllowedTasks(allowedTasks);
            task = taskQueue.newTask();
            textViewUpdate();

            findViewById(R.id.But_Del).setOnLongClickListener(v -> {
                answer = "";
                textViewUpdate();
                return true;
            });

            GridLayout parent = findViewById(R.id.buttonsLayout);
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child.getClass() == AppCompatButton.class && !child.getTag().equals("empty")) {

                    final String childtag = child.getTag().toString();
                    child.setOnLongClickListener(v -> {
                        numberPress(child);
                        return true;
                    });

                    boolean islinesym = childtag.equals("/") || childtag.equals(",");
                    child.setOnTouchListener((v, event) -> {
                        int resID = getResources().getIdentifier(!islinesym ? "line" + childtag : "linesym",
                                "id", id);
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

    // формирует вывод о прошедшем времени
    private String timeString(int count, int max) {
        return String.format("%s/%s", DateUtils.formatElapsedTime(count), DateUtils.formatElapsedTime(max));
    }

    //обновляет текстовый и визуальный таймеры
    void updateTimers() {
        G_progressBar.setProgress(progressStatus);
        timerText.setText(timeString(count, seconds));
    }

    //делаем все кнопки одинакового размера внутри GridLayout
    public void fillView(GridLayout parent) {
        //Button child;
        View child;
        for (int i = 0; i < parent.getChildCount(); i++) {
            child = parent.getChildAt(i);
            if (child.getClass() != View.class) {
                GridLayout.LayoutParams params = (GridLayout.LayoutParams) child.getLayoutParams();
                int margin = -8;
                if (child.getClass() == AppCompatButton.class)//|| child.getClass() == ImageButton.class)
                    params.setMargins(margin, margin, margin, margin);

                params.width = (parent.getWidth() / parent.getColumnCount()) - params.rightMargin - params.leftMargin;
                params.height = (parent.getHeight() / parent.getRowCount()) - params.bottomMargin - params.topMargin;

                child.setLayoutParams(params);
            }
        }

        Button sym;
        switch (tasktype) {
            case "integers":
                sym = findViewById(R.id.But_sym);
                sym.setText("");
                sym.setTag("empty");
                sym.setOnClickListener(null);
                sym.setOnTouchListener(null);
                View linesym = findViewById(R.id.linesym);
                linesym.setBackgroundColor(Color.TRANSPARENT);
                break;
            case "fractions":
                sym = findViewById(R.id.But_sym);
                sym.setTag("/");
                setModeSym();
                break;
            case "decimals":
            default:
//                sym.Text = ",";
//                sym.Tag = ",";
                break;
        }

        Enums.ButtonsPlace buttonsPlace = Enums.ButtonsPlace.convert(
                DataReader.GetInt(DataReader.BUTTONS_PLACE, this)
        );

        Enums.LayoutState layoutState = Enums.LayoutState.convert(
                DataReader.GetInt(DataReader.LAYOUT_STATE, this)
        );

        if (buttonsPlace == Enums.ButtonsPlace.LEFT)
            alterButtons();
        if (layoutState == Enums.LayoutState._123)
            alterLayout();
    }

    //меняет раскладку с 789 на 123
    void alterLayout() {
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

    //меняет местами два объекта
    void switchPlaces(View b1, View b2) {
        ViewGroup.LayoutParams temp = b1.getLayoutParams();
        b1.setLayoutParams(b2.getLayoutParams());
        b2.setLayoutParams(temp);
    }

    //перемещает кнопки-значки в левую сторону экрана
    void alterButtons() {
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
        b3 = findViewById(R.id.But_sym);
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
        b3 = findViewById(R.id.linesym);
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

        showTaskSetTrueAndRestartDisappearTimer();
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
                text.append(String.format("<strike>%s</strike>, ", s));
        }
        text.append(String.format("<strike>%s</strike>", answer));
        wrongAnswers.setText(Html.fromHtml(text.toString()));
    }

    //пропуск задания
    public void skipTask(View view) {
        showTaskSetTrueAndRestartDisappearTimer();
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

        if (Calendar.getInstance().getTimeInMillis() - tourStartTime >= millis)
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
        statistics.setText(String.format("Решено %d/%d (%d%%)", solved, shown - 1, solved * 100 / (shown - 1)));
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
                .setMessage(String.format("Решено заданий: %d/%d (%d%%)",
                        currentTour.getRightTasks(), currentTour.getTotalTasks(), 100 * currentTour.getRightTasks() / currentTour.getTotalTasks()))
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
        } else if ((answer.isEmpty() && symbol.equals(",")) ||
                (answer.contains(",") && symbol.equals(",")))
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
        ((Button) findViewById(R.id.But_sym)).setText(fractionSymsSpan[mode.ordinal()], TextView.BufferType.SPANNABLE);
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
    private void showTaskSetTrueAndRestartDisappearTimer() {
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
        showTaskSetTrueAndRestartDisappearTimer();
        TextView pressToShowTaskTV = findViewById(R.id.pressToShowTaskTV);
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