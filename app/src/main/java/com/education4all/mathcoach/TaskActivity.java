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

    //листнер для элементов шторки
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    //описание действий для кнопок шторки
    private void selectItem(int pos) {
        switch (pos) {
            case 0:
                showAnswer();
                break;
            case 1:
                skipTask();
                break;
            case 2:
                finishRound();
                break;
            case 3:
                cancelRound();
                break;
        }
        mDrawerLayout.closeDrawer(mDrawerList);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);

        //заполняем шторку
//        mDrawerList = (ListView)findViewById(R.id.left_drawer);
//        addDrawerItems();
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        Field mDragger = null;//mRightDragger for right obviously
//        try {
//            mDragger = mDrawerLayout.getClass().getDeclaredField(
//                    "mLeftDragger");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//        mDragger.setAccessible(true);
//        ViewDragHelper draggerObj = null;
//        try {
//            draggerObj = (ViewDragHelper) mDragger
//                    .get(mDrawerLayout);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        Field mEdgeSize = null;
//        try {
//            mEdgeSize = draggerObj.getClass().getDeclaredField(
//                    "mEdgeSize");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//        mEdgeSize.setAccessible(true);
//        int edge = 0;
//        try {
//            edge = mEdgeSize.getInt(draggerObj);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            mEdgeSize.setInt(draggerObj, edge * 10);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

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



    //таймер для конца раунда
    private Runnable endRound = new Runnable() {
        public void run() {
                new AlertDialog.Builder(TaskActivity.this)
                        .setTitle("Раунд завершён")
                        .setMessage("Время раунда вышло")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                newTask.timeTaken = (System.currentTimeMillis() - newTask.taskTime) / 1000;
                                currentTour.tourTasks.add(newTask);
                                ++currentTour.totalTasks;
                                currentTour.tourTime = (Calendar.getInstance().getTimeInMillis() - currentTour.tourDateTime) / 1000;
                                StatisticMaker.saveTour(currentTour, context);
                                finish();
                            }
                        })
                        .show();


        }
    };

    //вышло время раунда
    private void endRound() {
        newTask.timeTaken = (System.currentTimeMillis() - newTask.taskTime) / 1000;
        currentTour.tourTasks.add(newTask);
        currentTour.tourTime = (Calendar.getInstance().getTimeInMillis() - currentTour.tourDateTime) / 1000;
        currentTour.totalTasks = currentTour.tourTasks.size() + 1;
        StatisticMaker.saveTour(currentTour, context);
        new AlertDialog.Builder(TaskActivity.this)
                .setTitle("Раунд завершён")
                .setMessage("Решено " + Integer.toString(currentTour.rightTasks) + " из " + Integer.toString(currentTour.totalTasks) )
                .setCancelable(false)
                .setNeutralButton("Ещё один раунд", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                        startActivity(getIntent());
                    }
                })
                .setPositiveButton("В главное меню", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {

                     finish();
                   }
                })
                .setNegativeButton("Детальная статистика",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        int tourCount = StatisticMaker.getTourCount(context);
                        Intent i = new Intent(context, StatTourActivity.class);
                        i.putExtra("Tour", (Integer)(tourCount - 1));
                        startActivity(i);
                    }
                })
                .show();
    }

    //таймер для исчезновения задания
    private Runnable disapTask = new Runnable() {
        public void run() {
           showTask = false;
           textViewUpdate();

        }
    };




    //делаем все кнопки одинакового размера внутри GridLayout
    public void fillView(android.support.v7.widget.GridLayout parent)
    {
        Button child;
//
//        //Stretch buttons
//        int idealChildHeight = (int) ((gl.getHeight())/gl.getRowCount() - gl.getPaddingTop() - gl.getPaddingBottom());
//        int idealChildWidth = (int) ((gl.getWidth())/gl.getColumnCount() - gl.getPaddingLeft() - gl.getPaddingRight());
//        for (int i = 0;  i < gl.getChildCount(); i++) {
//            buttonTemp = (Button) gl.getChildAt(i);
//            buttonTemp.setWidth(idealChildWidth);
//            buttonTemp.setHeight(idealChildHeight);
//        }
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

    //нажатие на цифровую кнопку
    public void numberPress(View view) {
        if (!answerShown) {
            answer = answer + view.getTag();
            textViewUpdate();
        }
    }

    //обновляем поля вывода выражения
    private void textViewUpdate() {
        TextView expressionTV = (TextView)findViewById(R.id.expTextView);
        if (showTask) {
            expressionTV.setText(newTask.expression + " = " + answer);
        } else {
            //expressionTV.setText("Нажмите, чтобы показать задание" + " = " + answer);
            expressionTV.setText( " = " + answer);
            int x = expressionTV.getLeft();
            int y = expressionTV.getBottom() - expressionTV.getHeight() /4;
            Toast toast = Toast.makeText(this, "Нажмите, чтобы показать задание", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, x, y);
            toast.show();;

        }
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

    //нажатие на кнопку "ОК", проверяем правильность ответа и заносим в статистику
    public void okButtonClick(View view) {
        ++currentTour.totalTasks;
        if (Calendar.getInstance().getTimeInMillis() - tourStartTime >= millis) {
            endRound();
        } else {
            if (!answer.equals("")) {
                if (!answerShown) {
                    newTask.userAnswer += answer + ':' + (System.currentTimeMillis() - prevTaskTime) / 1000 + ',';
                    prevTaskTime = System.currentTimeMillis();
                } else {
                    newTask.userAnswer += "Показан ответ" + ':' + (System.currentTimeMillis() - prevTaskTime) / 1000 + ',';
                    prevTaskTime = System.currentTimeMillis();
                }
                if (answer.equals(newTask.answer)) {
                    newTask.timeTaken = (System.currentTimeMillis() - newTask.taskTime) / 1000;
                    if (!answerShown) {
                        ++currentTour.rightTasks;
                    }
                    currentTour.tourTasks.add(newTask);
                    newTask = new Task();
                    answerShown = false;
                    showTask = true;
                    newTask.generate(allowedTasks);
                    answer = "";
                    if (disapTime > -1) {
                        taskDisapHandler.postDelayed(disapTask, (long) (disapTime * 1000));
                    }
                    textViewUpdate();

                } else {
                    answer = "";
                    textViewUpdate();
                }
            }
        }
    }

    //нажатие кнопки назад
    @Override
    public void onBackPressed() {
//        roundTimeHandler.removeCallbacks(endRound);
//        new AlertDialog.Builder(TaskActivity.this)
//                .setTitle("Внимание!")
//                .setMessage("Завершить раунд без сохранения?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
//                .show();
        cancelRound();
    }

    //при выходи из активности необходимо остановить все таймеры
    @Override
    protected void onDestroy(){
        roundTimeHandler.removeCallbacks(endRound);
        taskDisapHandler.removeCallbacks(disapTask);
        super.onDestroy();
    }

    //показать ответ(вариант с кнопкой)
    public void showAnswer(View view) {
        showAnswer();
    }

    //показать ответ
    public void showAnswer() {
        answer = newTask.answer;
        answerShown = true;
        textViewUpdate();
    }

    //пропуск задания, вариант с кнопкой (view)
    public void skipTask(View view) {
        skipTask();
//        newTask.timeTaken = (System.currentTimeMillis() - newTask.taskTime) / 1000;
//        currentTour.tourTasks.add(newTask);
//        newTask = new Task();
//        answerShown = false;
//        showTask = true;
//        newTask.generate(allowedTasks);
//        answer = "";
//        if (disapTime > -1) {
//            taskDisapHandler.postDelayed(disapTask, (long)(disapTime*1000));
//        }
//        textViewUpdate();
    }

    //пропуск задания
    public void skipTask() {
        ++currentTour.totalTasks;
        newTask.userAnswer += "Задание пропущено:" + (System.currentTimeMillis() - prevTaskTime) / 1000 + ',';
        prevTaskTime = System.currentTimeMillis();
        newTask.timeTaken = (System.currentTimeMillis() - newTask.taskTime) / 1000;
        currentTour.tourTasks.add(newTask);
        newTask = new Task();
        answerShown = false;
        showTask = true;
        newTask.generate(allowedTasks);
        answer = "";
        if (disapTime > -1) {
            taskDisapHandler.postDelayed(disapTask, (long)(disapTime* 1000));
        }
        if ( Calendar.getInstance().getTimeInMillis() - tourStartTime >=  millis) {
            endRound();
        }
        textViewUpdate();

    }


    //завершение раунда с сохранением статистики (вариант с кнопкой - View)
    public void finishRound(View view) {
        finishRound();
//        new AlertDialog.Builder(TaskActivity.this)
//                .setTitle("Внимание!")
//                .setMessage("Завершить раунд?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        newTask.timeTaken = (System.currentTimeMillis() - newTask.taskTime) / 1000;
//                        currentTour.tourTasks.add(newTask);
//                        currentTour.tourTime = (Calendar.getInstance().getTimeInMillis() - currentTour.tourDateTime) / 1000;
//                        currentTour.totalTasks = currentTour.tourTasks.size() + 1;
//                        StatisticMaker.saveTour(currentTour, context);
//                        finish();
//                    }
//                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
//                .show();

    }

    //завершение раунда с сохранением статистики
    public void finishRound() {
        new AlertDialog.Builder(TaskActivity.this)
                .setTitle("Внимание!")
                .setMessage("Завершить раунд? Результаты будут сохранены")
                .setPositiveButton("Завершить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        newTask.timeTaken = (System.currentTimeMillis() - newTask.taskTime) / 1000;
                        currentTour.tourTasks.add(newTask);
                        currentTour.tourTime = (Calendar.getInstance().getTimeInMillis() - currentTour.tourDateTime) / 1000;
                        StatisticMaker.saveTour(currentTour, context);
                        finish();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();

    }

    //завершение раунда без сохранения (вариант с кнопкой, поэтому добавляется параметр View)
    public void cancelRound(View view) {
//        new AlertDialog.Builder(TaskActivity.this)
//                .setTitle("Внимание!")
//                .setMessage("Завершить раунд без сохранения?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
//                .show();
        cancelRound();
    }

    //завершение раунда без сохранения
    public void cancelRound() {
        new AlertDialog.Builder(TaskActivity.this)
                .setTitle("Внимание!")
                .setMessage("Завершить раунд без сохранения?")
                .setPositiveButton("Завершить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();

    }

    //просмотр ответа, если он исчез
    public void lookAnswer(View view) {
        showTask = true;
        if (disapTime > -1) {
            taskDisapHandler.postDelayed(disapTask, (long) (disapTime * 1000));
        }
        textViewUpdate();
    }

    //заполняем шторку
//    private void addDrawerItems() {
//        String[] osArray = { "Показать ответ", "Пропустить задание", "Закончить упражнение", "Прервать упражнение"};
//        mAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, osArray);
//        mDrawerList.setAdapter(mAdapter);
//    }


    public void crossClick(View view) {
        new AlertDialog.Builder(TaskActivity.this)
                .setTitle("Внимание!")
                .setMessage("Завершить раунд")
                .setPositiveButton("Без сохранения", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNeutralButton("С сохранением", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    newTask.timeTaken = (System.currentTimeMillis() - newTask.taskTime) / 1000;
                    currentTour.tourTasks.add(newTask);
                    currentTour.tourTime = (Calendar.getInstance().getTimeInMillis() - currentTour.tourDateTime) / 1000;
                    StatisticMaker.saveTour(currentTour, context);
                    finish();
                }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}
