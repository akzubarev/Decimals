package com.education4all;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.education4all.MathCoachAlg.StatisticMaker;

import com.education4all.MathCoachAlg.Tasks.Task;
import com.education4all.MathCoachAlg.Tours.Tour;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StatiscticsActivity extends AppCompatActivity {
    private final Context l_context = this;
    ArrayList<Integer> stat = new ArrayList<>();
    //deprecated
    final View.OnClickListener tourClick = v -> {
        Intent i = new Intent(l_context, StatTourActivity.class);
        i.putExtra("Tour", (Integer) (v.getTag()));
        startActivity(i);
    };

    public void tourClick(View v) {
        Intent i = new Intent(l_context, StatTourActivity.class);
        i.putExtra("Tour", (Integer) (v.getTag()));
        startActivity(i);
    }

    public View generateBar() {
//        View bar = new View(this);
//        bar.setVisibility(View.VISIBLE);
//        bar.setMinimumHeight(5);
//        bar.setBackgroundColor(getResources().getColor(R.color.shadowed));
//        bar.setPadding(50, 0, 50, 0);
        return View.inflate(l_context, R.layout.tourbar, null);
    }

    RelativeLayout deprecatedRowGen(LinearLayout tourLayout, int tourNumber) {
        View bar = new View(this);
        bar.setVisibility(View.VISIBLE);
        bar.setMinimumHeight(5);
        bar.setBackgroundColor(getResources().getColor(R.color.shadowed));
        bar.setPadding(50, 0, 50, 0);

        RelativeLayout row = new RelativeLayout(this);

        String tourInfoStr = StatisticMaker.getTourInfo(this, tourNumber);
        String txt = Tour.DepictTour(tourInfoStr);
        Typeface font = Typeface.create("sans-serif-light", Typeface.NORMAL);

        boolean isAllTasksRight = (txt.substring(0, 1).equals("="));
        int divider = txt.indexOf("Решено");
        int end = txt.indexOf("сек");
        String datetime = txt.substring(1, divider - 1);
        String info = "";
        if (isAllTasksRight)
            info = getString(R.string.star) + " ";
        info += txt.substring(divider, end);
        // info = "★ " + txt.substring(divider);


        TextView tourdatetime = new TextView(this);
        tourdatetime.setId(tourNumber + 1);
        tourdatetime.setText(datetime);
        tourdatetime.setTag(tourNumber);
        tourdatetime.setTextSize(getResources().getDimension(R.dimen.dimen5) / getResources().getDisplayMetrics().density);
        tourdatetime.setOnClickListener(tourClick);
        tourdatetime.setGravity(Gravity.TOP | Gravity.START);
        tourdatetime.setTypeface(font);

        TextView tourinfo = new TextView(this);
        tourinfo.setId(tourNumber);
        tourinfo.setText(info);
        tourinfo.setTextSize(getResources().getDimension(R.dimen.dimen4) / getResources().getDisplayMetrics().density);
        tourinfo.setOnClickListener(tourClick);
        tourinfo.setTag(tourNumber);
        tourinfo.setGravity(Gravity.BOTTOM | Gravity.START);
        tourinfo.setTypeface(font);


        Button arrow = new Button((this));
        arrow.setTag(tourNumber);
        arrow.setOnClickListener(tourClick);
        arrow.setText(getString(R.string.arrow));
        arrow.setPadding(0, 0, 0, 10);
        arrow.setTextSize(getResources().getDimension(R.dimen.dimen1) / getResources().getDisplayMetrics().density);
        arrow.setBackgroundColor(Color.TRANSPARENT);
        arrow.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        arrow.setTextColor(ContextCompat.getColor(this, R.color.additional));

        row.setGravity(Gravity.CENTER_VERTICAL);

        tourdatetime.setTextColor(ContextCompat.getColor(this, R.color.main));
        tourinfo.setTextColor(ContextCompat.getColor(this, R.color.main));

//            if (isAllTasksRight)
//                tourinfo.setTypeface(tourinfo.getTypeface(), Typeface.ITALIC);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        arrow.setLayoutParams(layoutParams);


        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.BELOW, tourdatetime.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (!isAllTasksRight)
            layoutParams.leftMargin = 50;
        tourinfo.setLayoutParams(layoutParams);

        row.addView(tourdatetime);
        row.addView(tourinfo);
        row.addView(arrow);
        tourLayout.addView(row);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 20);
        row.setLayoutParams(params);

        bar = new View(this);
        bar.setVisibility(View.VISIBLE);
        bar.setMinimumHeight(5);
        bar.setPadding(50, 0, 50, 0);
        bar.setBackgroundColor(getResources().getColor(R.color.shadowed));
        tourLayout.addView(bar);
        return row;
    }

    Pair<String, String> getTourInfo(String tourdepiction) {
        boolean isAllTasksRight = (tourdepiction.substring(0, 1).equals("="));
        int divider = tourdepiction.indexOf("Решено");
        int end = tourdepiction.indexOf("сек");
        String datetime = tourdepiction.substring(1, divider - 1);
        String info = "";
        if (isAllTasksRight)
            info = getString(R.string.star) + " ";
        info += tourdepiction.substring(divider, end);
        return new Pair<>(datetime, info);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statisctics);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        // myToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_trash));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        int tourCount = StatisticMaker.getTourCount(this);
        ScrollView Tours = findViewById(R.id.scrollView);
        LinearLayout layout = findViewById(R.id.tourlayout);

        View bar = generateBar();
        layout.addView(bar);

        for (int tourNumber = tourCount - 1; tourNumber >= 0; --tourNumber) {
            //RelativeLayout row = new RelativeLayout(this);
            RelativeLayout row = new RelativeLayout(this);
            RelativeLayout.inflate(l_context, R.layout.tourblock, row);
            row.setBackgroundColor(Color.TRANSPARENT);


            String tourInfoStr = StatisticMaker.getTourInfo(this, tourNumber);
            Pair<String, String> infopair = getTourInfo(Tour.DepictTour(tourInfoStr));

            String info = infopair.first,
                    datetime = infopair.second;

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

        setUpChart();
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
        if (item.getItemId() == R.id.action_delete_stats) {
            if (StatisticMaker.getTourCount(this) > 0) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Удаление результатов")
                        .setMessage("Вы уверены? Это действие нельзя будет отменить.")

                        .setNegativeButton("Отменить", (dialog1, which) -> {
                            //finish();
                        })
                        .setPositiveButton("Удалить", (dialog12, which) -> DeleteStatistics())
                        .show();

                CommonOperations.FixDialog(dialog, getApplicationContext()); // почему-то нужно для планшетов
            }
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    protected void setUpChart() {
        readChartData();
        LineChart chart = findViewById(R.id.chart);

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
        xAxis.setAxisMaximum(stat.size()/2);
        xAxis.setLabelCount(stat.size()/2);

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


        LineData data = generateDataFromArray(stat);
        chart.setData(data);
        chart.invalidate();
        // chart.animateX(750);
    }

    void readChartData() {
        if (StatisticMaker.getTourCount(this) > 0) {
            for (int tourNumber = StatisticMaker.getTourCount(this)-1; tourNumber >= 0; tourNumber--) {
                String tourInfoStr = StatisticMaker.getTourInfo(this, tourNumber);
                String txt = Tour.DepictTour(tourInfoStr);
                int divider = txt.indexOf("Решено");

                Tour tourinfo = StatisticMaker.loadTour(this, tourNumber);
                ArrayList<String> deTour = tourinfo.serialize();

                int jump = 0; // костыль из прошлых версий
                for (int i = deTour.size() - 2; i > 0; i--) {
                    ArrayList<String> answers = new ArrayList<>();
                    Task currentTask = Task.makeTask(deTour.get(i));
                    ArrayList<String> TaskDepiction = Task.DepictTaskExtended(deTour.get(i), answers);

                    String datetime = txt.substring(1, divider - 1);
                    String date = datetime.substring(0, 10);

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

                    boolean right = answers.get(0).equals(currentTask.getAnswer());

                    stat.add(0, right ? 1 : 0);
                    if (stat.size() == 20)
                        break;
                }
            }
        }

        if (stat.size() < 4) {
            stat.clear();
            CommonOperations.genereteFakeTour("10 1 1 1 1 1 0 1 1 1 1 1 1 1 1 1 0 1 0 1 1", this);
            readChartData();
        }
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
}
