package com.education4all.mathcoach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import java.lang.Runnable;
import java.lang.reflect.Field;
import java.util.Calendar;

import android.preference.DialogPreference;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.education4all.mathcoach.MathCoachAlg.DataReader;
import com.education4all.mathcoach.MathCoachAlg.StatisticMaker;

import MathCoachAlg.Task;
import MathCoachAlg.Tour;


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
    private boolean answerShown = false; // показан и ответ
    private boolean showTask = true; // показано ли задание
    private float disapTime; // время изсчезновения задания
    private int[][] allowedTasks; // массив разрешённых заданий
    private ListView mDrawerList; // шторка внутренности
    private DrawerLayout mDrawerLayout; // сама шторка
    private ArrayAdapter<String> mAdapter; // адаптер для фоматирования строк
    long prevTaskTime;
    long tourStartTime; //время начала раунда
    long millis; //время раунда в миллисекундах


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);

        //заполняем GridLayout
        android.support.v7.widget.GridLayout gl = (android.support.v7.widget.GridLayout)findViewById(R.id.buttonsLayout);
        ViewTreeObserver vto = gl.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {@Override public void onGlobalLayout() {
        android.support.v7.widget.GridLayout gl = (android.support.v7.widget.GridLayout) findViewById(R.id.buttonsLayout);
        fillView(gl);
            ViewTreeObserver obs = gl.getViewTreeObserver();
            obs.removeGlobalOnLayoutListener(this);
        }});

        //обнуляем переменные и инициализируем элементы layout
        Intent intent = getIntent();
        G_progressBar = (ProgressBar)findViewById(R.id.taskProgress);
        newTask = new Task();
        allowedTasks = DataReader.readAllowedTasks(this);
        tourStartTime = Calendar.getInstance().getTimeInMillis();
        answer = new String();
        RoundTime = DataReader.GetRoundTime(this);
        if (!newTask.areTasks(allowedTasks)) {
            new AlertDialog.Builder(TaskActivity.this)
                    .setTitle("Ошибка")
                    .setMessage("Не выбраны задания для генерации")
                    .setCancelable(false)
                    .setPositiveButton("Настройки", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            Intent intent = new Intent(context, SettingsSimpleActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        } else {
            newTask.generate(allowedTasks);
        }
        millis =(long)(RoundTime * 1000 * 60);
        final Context l_context = this;
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            G_progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        Thread.sleep((long)(RoundTime * 600));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        disapTime = DataReader.GetDisapRoundTime(this);

        //roundTimeHandler.postDelayed(endRound, millis);
        if (disapTime > -1) {
            taskDisapHandler.postDelayed(disapTask, (long)(disapTime*1000));
        }
        textViewUpdate();
        currentTour.totalTasks = 0;
        prevTaskTime = tourStartTime;
    }

    //делаем все кнопки одинакового размера внутри GridLayout
    public void fillView(android.support.v7.widget.GridLayout parent) {
        Button child;
        for (int i = 0;  i < parent.getChildCount(); i++) {
            child = (Button)parent.getChildAt(i);
            android.support.v7.widget.GridLayout.LayoutParams params = (android.support.v7.widget.GridLayout.LayoutParams) child.getLayoutParams();
            int margin = -8;
            params.setMargins(margin, margin, margin, margin);
            params.width = (parent.getWidth() / parent.getColumnCount()) - params.rightMargin - params.leftMargin;
            params.height = (parent.getHeight() / parent.getRowCount()) - params.bottomMargin - params.topMargin;
            child.setLayoutParams(params);
            child.setBackgroundColor(parent.getDrawingCacheBackgroundColor());
        }
    }

    //сохраняем информацию о данной попытке и обновляем информацию раунда
    private void saveTaskStatistic() {
        if (answer.equals(newTask.answer)) ++currentTour.rightTasks; // увеличиваем счетчик правильных заданий, если ответ правильный
        newTask.userAnswer += answer + ":" + (System.currentTimeMillis() - prevTaskTime) / 1000 + ',';
        prevTaskTime = System.currentTimeMillis();
        newTask.timeTaken = (System.currentTimeMillis() - newTask.taskTime) / 1000; //??? вычисляем время, потраченное пользователем на ПРАВИЛЬНОЕ решение текущего задания
        currentTour.tourTasks.add(newTask); //сохраняем информацию о текущем задании
        currentTour.tourTime = (Calendar.getInstance().getTimeInMillis() - currentTour.tourDateTime) / 1000; //обновляем реальную продолжительность раунда
        ++currentTour.totalTasks; //увеличиваем счетчик количества заданий в раунде
    }

    //нажатие на кнопку "ОК", проверяем правильность ответа и заносим в статистику
    public void okButtonClick(View view) {
        if (answer.equals("")) return;
        if (answerShown) {
            answer = "...";
            answerShown = false;
            textViewUpdate();
        }
        saveTaskStatistic();

        if (answer.equals(newTask.answer)) {
            startNewTask();
        } else {
            answer = "";
            textViewUpdate();
        }
    }

    //пропуск задания
    public void skipTask(View view) {
        answer = "...";
        saveTaskStatistic();
        startNewTask();
    }

    //запуск нового задания
    private void startNewTask() {
        newTask = new Task();
        answerShown = false;
        showTask = true;
        newTask.generate(allowedTasks);
        answer = "";
        if (disapTime > -1) {
            taskDisapHandler.postDelayed(disapTask, (long) (disapTime * 1000));
        }
        if (Calendar.getInstance().getTimeInMillis() - tourStartTime >= millis) {
            endRound();
        } else {
            textViewUpdate();
        }
    }

    //завершение раунда по окончанию таймера
    private void endRound() {
//        saveTaskStatistic(); // Здесь сохранять статистику задания не нужно!
        StatisticMaker.saveTour(currentTour, context);
        new AlertDialog.Builder(TaskActivity.this)
                .setTitle("Раунд завершён")
                .setMessage("Решено заданий: " + Integer.toString(currentTour.rightTasks) + " из " + Integer.toString(currentTour.totalTasks) )
                .setCancelable(false)
                .setNeutralButton("Ещё раунд", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                })
                .setNegativeButton("Результат",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        int tourCount = StatisticMaker.getTourCount(context);
                        Intent i = new Intent(context, StatTourActivity.class);
                        i.putExtra("Tour", (Integer)(tourCount - 1));
                        startActivity(i);
                    }
                })
                .setPositiveButton("В начало", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    //досрочное завершение раунда по нажатию выхода
    public void crossClick(View view) {
        if (currentTour.totalTasks == 0) {
            finish();
        } else {
            new AlertDialog.Builder(TaskActivity.this)
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
                    })
                    .show();
        }
    }

    //обновляем поля вывода выражения
    private void textViewUpdate() {
        TextView expressionTV = (TextView)findViewById(R.id.expTextView);
        if (showTask) {
            expressionTV.setText(newTask.expression + " = " + answer);
        } else {
            expressionTV.setText( " = " + answer);
            int x = expressionTV.getLeft();
            int y = expressionTV.getBottom() - expressionTV.getHeight() /4;
            Toast toast = Toast.makeText(this, "Нажмите, чтобы показать задание", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, x, y);
            toast.show();;
        }
    }

    //нажатие на цифровую кнопку
    public void numberPress(View view) {
        if (answerShown && answer.length() > 0) {
            return;
        }
        if (answer.equals("0")) {
            answer = "";
            textViewUpdate();
        }
        answer = answer + view.getTag();
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
        textViewUpdate();
    }

    //таймер для исчезновения задания
    private Runnable disapTask = new Runnable() {
        public void run() {
            showTask = false;
            textViewUpdate();
        }
    };

    //просмотр ответа, если он исчез
    public void lookAnswer(View view) {
        showTask = true;
        if (disapTime > -1) {
            taskDisapHandler.postDelayed(disapTask, (long) (disapTime * 1000));
        }
        textViewUpdate();
    }

    //при выходе из активности необходимо остановить все таймеры
    @Override
    protected void onDestroy(){
        taskDisapHandler.removeCallbacks(disapTask);
        super.onDestroy();
    }
}