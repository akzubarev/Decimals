package com.education4all.activities;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.education4all.R;
import com.education4all.TourAdapter;
import com.education4all.Utils;
import com.education4all.mathCoachAlg.StatisticMaker;

import com.education4all.mathCoachAlg.tasks.Task;
import com.education4all.mathCoachAlg.tours.Tour;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StatiscticsActivity extends AppCompatActivity {
    private final Context l_context = this;
    ArrayList<Integer> stat = new ArrayList<>();
    ArrayList<Tour> tours = new ArrayList<>();
    int statistics = 0;
    int statsize = 10;

    private FirebaseDatabase database;
    private DatabaseReference usersDBR;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public void tourClick(View v) {
        Intent i = new Intent(l_context, StatTourActivity.class);
        i.putExtra("Tour", (Integer) (v.getTag()));
        startActivity(i);
    }

    public View generateBar() {
        return View.inflate(l_context, R.layout.tourbar, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statisctics);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        usersDBR = database.getReference().child("users");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  LineChart chart = findViewById(R.id.chart);
//        LinearLayout progressLL = findViewById(R.id.graphlayout);
//        LinearLayout layout = findViewById(R.id.tourlayout);
//        layout.removeAllViews();
//        layout.addView(progressLL);
        //    layout.addView(chart);

        //  try {
        getUserStats(userID);
        // setUpChart();
        setupProgress(findViewById(R.id.toggle));
//        } catch (Exception e) {
////            chart = findViewById(R.id.chart);
////            chart.setNoDataText("Обновите статистику, чтобы увидеть результаты");
////            chart.setNoDataTextColor(Color.WHITE);
////            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
////            chart.setLayoutParams(params);
//
//            Log.d("EXCEPTION", e.getMessage());
//            AlertDialog dialog = new AlertDialog.Builder(this)
//                    .setMessage("Обновите статистику, чтобы увидеть результаты")
//                    .setCancelable(false)
//                    .setPositiveButton("Обновить", (dialog1, which) -> {
//                        Utils.updateStatistics(this);
//                        startActivity(getIntent());
//                    })
//                    .setNegativeButton("Назад", (dialog1, which) -> finish()).create();
//            dialog.show();
//            Utils.FixDialog(dialog, getApplicationContext()); // почему-то нужно для планшетов
//        }
    }

    public void fill() {
        if (true) { //TODO get "else" branch to work as it should
//            if (tours.isEmpty()) {
            if (false) {
                int tourCount = StatisticMaker.getTourCount(this);
                for (int tourNumber = 0; tourNumber < tourCount; tourNumber++)
                    tours.add(StatisticMaker.loadTour(this, tourNumber));
            }
            oldFill(tours);
        } else {
            RecyclerView tourlist = findViewById(R.id.tourlist);
            tourlist.setHasFixedSize(true);
            tourlist.addItemDecoration(new DividerItemDecoration(
                    tourlist.getContext(), DividerItemDecoration.VERTICAL));
            LinearLayoutManager userLayoutManager = new LinearLayoutManager(this);
            TourAdapter tourAdapter = new TourAdapter(tours, this);

            tourlist.setLayoutManager(userLayoutManager);
            tourlist.setAdapter(tourAdapter);

            tourAdapter.setOnUserClickListener(position -> {
                Intent i = new Intent(l_context, StatTourActivity.class);
                i.putExtra("Tour", position);
                startActivity(i);
            });
        }
    }

    public void oldFill(ArrayList<Tour> tours) {

        LinearLayout layout = findViewById(R.id.tourlayout);

        View bar = generateBar();
        layout.addView(bar);

        for (int tourNumber = tours.size() - 1; tourNumber >= 0; --tourNumber) {
            RelativeLayout row = new RelativeLayout(this);
            RelativeLayout.inflate(l_context, R.layout.tourblock, row);
            row.setBackgroundColor(Color.TRANSPARENT);


            Tour tour = tours.get(tourNumber);
            String info = tour.info();
            String datetime = tour.dateTime();

            if (tour.getRightTasks() == tour.getTotalTasks())
                info = getString(R.string.star) + " " + info;


            TextView tourdatetime = row.findViewById(R.id.tourdatetime);
            tourdatetime.setId(tourNumber + 1);
            tourdatetime.setText(datetime);
            tourdatetime.setTag(tourNumber);

            TextView tourinfo = row.findViewById(R.id.tourinfo);
            tourinfo.setId(tourNumber);
            tourinfo.setText(info);
            tourinfo.setTag(tourNumber);

            Button arrow = row.findViewById(R.id.arrow);
            arrow.setTag(tourNumber);

            bar = generateBar();

            layout.addView(row);
            layout.addView(bar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_statisctics, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void DeleteStatistics() {
        finish();
        StatisticMaker.removeStatistics(this);
        Intent intent = getIntent();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_stats:
                if (StatisticMaker.getTourCount(this) > 0) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("Удаление результатов")
                            .setMessage("Вы уверены? Это действие нельзя будет отменить.")

                            .setNegativeButton("Отменить", (dialog1, which) -> {
                                //finish();
                            })
                            .setPositiveButton("Удалить", (dialog12, which) -> DeleteStatistics())
                            .show();

                    Utils.FixDialog(dialog, getApplicationContext()); // почему-то нужно для планшетов
                }
                return true;
            case R.id.action_upload_stats:
                if (tours.isEmpty()) {
                    for (int tourNumber = StatisticMaker.getTourCount(this) - 1; tourNumber >= 0; --tourNumber)
                        tours.add(StatisticMaker.loadTour(this, tourNumber));
                    usersDBR.child(userID).child("statistics").setValue(tours);
                    fill();
                }
                return true;
            case R.id.action_another_user:
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Введите ID пользователя").setView(input).setPositiveButton("Ок", (dialog1, which) ->
                                getUserStats(input.getText().toString())
                        ).setNegativeButton("Отмена", (dialog1, which) -> {
                        })
                        .show();
                Utils.FixDialog(dialog, getApplicationContext()); // почему-то нужно для планшетов
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void getUserStats(String id) {
        usersDBR.child(id).child("statistics").get().addOnCompleteListener(
                (com.google.android.gms.tasks.Task<DataSnapshot> task) -> {
                    tours.clear();
                    for (DataSnapshot postSnapshot : task.getResult().getChildren()) {
                        Tour tour = postSnapshot.getValue(Tour.class);
                        tours.add(tour);
                    }
                    fill();
                }
        );

    }

    public void setupProgress(View v) {
        ToggleButton toggle = (ToggleButton) v;
        statsize = toggle.isChecked() ? 100 : 10;
        readProgressData();
        com.warkiz.widget.IndicatorSeekBar pb = findViewById(R.id.progress);
        if (statsize == 10) {
            pb.setMin(0);
            pb.setMax(10);
            pb.setProgress(statistics);
            pb.customTickTexts(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "     10"});
        } else {
            pb.setMin(90);
            pb.setMax(100);
            pb.customTickTexts(new String[]{"90    ", "91", "92", "93", "94", "95", "96", "97", "98", "99", "     100"});
            pb.setProgress(statistics > 90 ? (statistics - 90) * 10 : 0);
        }
    }

    void readProgressData() {
        int count = 0;
        statistics = 0;
        if (StatisticMaker.getTourCount(this) > 0) {
            for (int tourNumber = StatisticMaker.getTourCount(this) - 1; tourNumber >= 0; tourNumber--) {
                Tour tour = StatisticMaker.loadTour(this, tourNumber);

                for (int i = tour.getTotalTasks() - 1; i >= 0; i--) {
                    Task task = tour.getTourTasks().get(i);

                    statistics += task.correct() ? 1 : 0;
                    count++;

                    if (count == statsize)
                        return;
                }
            }
        }
    }

    // region chart
    protected void setUpChart() {
        readChartData();
        LineChart chart = new LineChart(this);// = findViewById(R.id.chart);
        configureChart(chart);

        if (stat.size() >= 4) {
            LineData data = generateDataFromArray(stat);
            chart.setData(data);
        } else {
            chart.setNoDataText("Недостаточно данных для постройки графика");
            chart.setNoDataTextColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            chart.setLayoutParams(params);
        }
        chart.invalidate();
        // chart.animateX(750);
    }

    private void configureChart(LineChart chart) {
        // enable touch gestures
        chart.setTouchEnabled(true);
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

//        chart.setBackgroundColor(Color.WHITE);
//        chart.setDrawGridBackground(false);

        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(stat.size() / 2);
        xAxis.setLabelCount(stat.size() / 2);

        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(100);
        leftAxis.setLabelCount(10);

        chart.getAxisRight().setEnabled(false);

        Legend l = chart.getLegend();
        l.setEnabled(false);
    }

    void readChartData() {
        if (StatisticMaker.getTourCount(this) > 0) {
            for (int tourNumber = StatisticMaker.getTourCount(this) - 1; tourNumber >= 0; tourNumber--) {
                Tour tour = StatisticMaker.loadTour(this, tourNumber);

                for (int i = tour.getTotalTasks() - 1; i >= 0; i--) {
                    Task task = tour.getTourTasks().get(i);

                    stat.add(0, task.correct() ? 1 : 0);
                    if (stat.size() == 20)
                        return;
                }
            }
        }

//        if (stat.size() < 4) {
////            stat.clear();
////            CommonOperations.genereteFakeTour("10 1 1 1 1 1 0 1 1 1 1 1 1 1 1 1 0 1 0 1 1", this);
////            readChartData();
//        }
    }

    private LineData generateDataFromArray(ArrayList<Integer> last20tasks) {
        ArrayList<Entry> values = new ArrayList<>();

        int pool = last20tasks.size() / 2;
        int border = pool - 1;
        int stat = 0;
        for (int i = 0; i < last20tasks.size(); i++) {
            stat += last20tasks.get(i);
            if (i >= border) {
                stat -= last20tasks.get(i - border);
                values.add(new Entry(i - border, Math.round(100 * stat / pool)));
            }
        }

        LineDataSet data = new LineDataSet(values, "New DataSet");
        data.setLineWidth(2.5f);
        data.setDrawCircles(false);
        data.setCircleRadius(0);
        data.setHighLightColor(Color.rgb(244, 117, 117));
        data.setDrawValues(false);
        return new LineData(data);
    }

    private LineData generateRandomData() {

        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            values1.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d1 = new LineDataSet(values1, "New DataSet");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(0);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

//        ArrayList<Entry> values2 = new ArrayList<>();
//
//        for (int i = 0; i < 12; i++) {
//            values2.add(new Entry(i, values1.get(i).getY() - 30));
//        }
//
//        LineDataSet d2 = new LineDataSet(values2, "New DataSet " + cnt + ", (2)");
//        d2.setLineWidth(2.5f);
//        d2.setCircleRadius(4.5f);
//        d2.setHighLightColor(Color.rgb(244, 117, 117));
//        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
//        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
//        d2.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        //sets.add(d2);

        return new LineData(d1);
    }
    //endregion
}
