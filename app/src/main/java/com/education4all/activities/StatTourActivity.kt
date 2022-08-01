package com.education4all.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.education4all.R;
import com.education4all.firebase.FireBaseUtils;
import com.education4all.utils.Utils;
import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tasks.Task;
import com.education4all.mathCoachAlg.tours.Tour;


public class StatTourActivity extends AppCompatActivity {
    int TourNumber;

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
            LinearLayout layout = findViewById(R.id.linear);
            Tour tour = StatisticMaker.loadTour(this, TourNumber);


            String line = String.format("%s %d/%d (%d%%)",
                    getString(R.string.star),
                    tour.getRightTasks(),
                    tour.getTotalTasks(),
                    tour.getRightTasks() * 100 / tour.getTotalTasks());
            getSupportActionBar().setTitle(line);

            for (Task task : tour.getTourTasks()) {
                LinearLayout row = new LinearLayout(this);
                LinearLayout.inflate(this, R.layout.stat_tour_block, row);
                TextView taskview = row.findViewById(R.id.task);

                TextView seconds = row.findViewById(R.id.seconds);
                String taskAndUserAnswer = task.getExpression() + " = " + task.getUserAnswer();

                taskview.setText(taskAndUserAnswer);

                boolean userAnswerIsCorrect = task.correct();
                if (userAnswerIsCorrect)
                    taskview.setTextColor(ContextCompat.getColor(this, R.color.main));
                else
                    taskview.setTextColor(ContextCompat.getColor(this, R.color.shadowed));

                String userTime = task.getUserAnswerTime() + " сек.";
                seconds.setText(userTime);

                layout.addView(row);
            }

        } else
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stat_tour, menu);
        return true;
    }


    private void deleteTour() {
        StatisticMaker.removeTour(this, TourNumber);
//        Intent intent = new Intent(this, StatiscticsActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        new FireBaseUtils().updateTours(getApplicationContext());
        //startActivity(intent);
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
                    .setPositiveButton("Удалить", (dialog12, which) -> deleteTour())
                    .show();

            Utils.FixDialog(dialog, getApplicationContext()); // почему-то нужно для планшетов
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}