package com.education4all.activities

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.education4all.R
import com.education4all.firebase.FireBaseUtils
import com.education4all.firebase.FireBaseUtils.StatisticsCallback
import com.education4all.mathCoachAlg.StatisticMaker
import com.education4all.mathCoachAlg.tasks.*
import com.education4all.mathCoachAlg.tours.Tour
import com.education4all.utils.OldFormatHandler
import com.education4all.utils.Utils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.warkiz.widget.IndicatorSeekBar
import java.util.*

class StatiscticsActivity : AppCompatActivity() {
    private val l_context: Context = this
    var stat = ArrayList<Int>()
    var tours = ArrayList<Tour?>()
    var statistics = 0
    var statsize = 10
    var callback = StatisticsCallback { loaded_tours: ArrayList<Tour?> ->
        tours = loaded_tours
        //        fill();
        oldFill(tours)
    }

    fun tourClick(v: View) {
        val i = Intent(l_context, StatTourActivity::class.java)
        i.putExtra("Tour", v.tag as Int)
        startActivity(i)
    }

    fun generateBar(): View {
        return View.inflate(l_context, R.layout.tourbar, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statisctics)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (Utils.versioningTool() == Utils.VERSIONING_REMOVEFIREBASE) findViewById<View>(R.id.graphlayout).visibility =
            View.GONE
    }

    override fun onResume() {
        super.onResume()
        try {
            fill()
            FireBaseUtils().getUserStats { tours: ArrayList<Tour?>? -> }
            setupProgress(findViewById(R.id.toggle))
            // setUpChart();
        } catch (e: Exception) {
            Log.e("Old data format", e.message!!)
            oldFormat()
        }
    }

    private fun oldFormat() {
//        LineChart chart = findViewById(R.id.chart);
//        chart.setNoDataText("Обновите статистику, чтобы увидеть результаты");
//        chart.setNoDataTextColor(Color.WHITE);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
//        chart.setLayoutParams(params);
        val dialog = AlertDialog.Builder(this)
            .setMessage("Обновите статистику, чтобы увидеть результаты")
            .setCancelable(false)
            .setPositiveButton("Обновить") { dialog1: DialogInterface?, which: Int ->
                OldFormatHandler.updateStatistics(this)
                startActivity(intent)
            }
            .setNegativeButton("Назад") { dialog1: DialogInterface?, which: Int -> finish() }
            .create()
        dialog.show()
        Utils.FixDialog(dialog, applicationContext) // почему-то нужно для планшетов
    }

    fun fill() {

//            if (tours.isEmpty()) {
        val debugFirebase = false
        if (!debugFirebase) {
            tours.clear()
            val tourCount = StatisticMaker.getTourCount(this)
            for (tourNumber in 0 until tourCount) tours.add(
                StatisticMaker.loadTour(
                    this,
                    tourNumber
                )
            )
        }
        oldFill(tours)
        //        RecyclerView tourlist = findViewById(R.id.tourlist);
//        tourlist.setHasFixedSize(true);
//        tourlist.addItemDecoration(new DividerItemDecoration(
//                tourlist.getContext(), DividerItemDecoration.VERTICAL));
//        LinearLayoutManager userLayoutManager = new LinearLayoutManager(this);
//        TourAdapter tourAdapter = new TourAdapter(tours, this);
//
//        tourlist.setLayoutManager(userLayoutManager);
//        tourlist.setAdapter(tourAdapter);
//
//        tourAdapter.setOnUserClickListener(position -> {
//            Intent i = new Intent(l_context, StatTourActivity.class);
//            i.putExtra("Tour", position);
//            startActivity(i);
//        });
    }

    fun oldFill(tours: ArrayList<Tour?>) {
//        LineChart chart = findViewById(R.id.chart);
        val progressLL = findViewById<LinearLayout>(R.id.graphlayout)
        val layout = findViewById<LinearLayout>(R.id.tourlayout)
        layout.removeAllViews()
        layout.addView(progressLL)
        //        layout.addView(chart);
        var bar = generateBar()
        layout.addView(bar)
        for (tourNumber in tours.indices.reversed()) {
            val row = RelativeLayout(this)
            RelativeLayout.inflate(l_context, R.layout.tourblock, row)
            row.setBackgroundColor(Color.TRANSPARENT)
            val tour = tours[tourNumber]
            var info = tour!!.info()
            val datetime = tour.dateTime()
            if (tour.rightTasks == tour.totalTasks) info = getString(R.string.star) + " " + info
            val tourdatetime = row.findViewById<TextView>(R.id.tourdatetime)
            tourdatetime.id = tourNumber + 1
            tourdatetime.text = datetime
            tourdatetime.tag = tourNumber
            val tourinfo = row.findViewById<TextView>(R.id.tourinfo)
            tourinfo.id = tourNumber
            tourinfo.text = info
            tourinfo.tag = tourNumber
            val arrow = row.findViewById<Button>(R.id.arrow)
            arrow.tag = tourNumber
            bar = generateBar()
            layout.addView(row)
            layout.addView(bar)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_statisctics, menu)
        if (Utils.versioningTool() == Utils.VERSIONING_REMOVEFIREBASE) {
            val another_user = menu.findItem(R.id.action_another_user)
            another_user.isVisible = false
        }
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    fun DeleteStatistics() {
        StatisticMaker.removeStatistics(this)
        tours.clear()
        oldFill(tours)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_stats -> {
                if (StatisticMaker.getTourCount(this) > 0) {
                    val dialog = AlertDialog.Builder(this)
                        .setTitle("Удаление результатов")
                        .setMessage("Вы уверены? Это действие нельзя будет отменить.")
                        .setNegativeButton("Отменить") { dialog1: DialogInterface?, which: Int -> }
                        .setPositiveButton("Удалить") { dialog12: DialogInterface?, which: Int -> DeleteStatistics() }
                        .show()
                    Utils.FixDialog(dialog, applicationContext) // почему-то нужно для планшетов
                }
                true
            }
            R.id.action_another_user -> {
                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Введите ID пользователя").setView(input).setPositiveButton(
                        "Ок"
                    ) { dialog1: DialogInterface?, which: Int ->
                        FireBaseUtils().getUserStats(
                            input.text.toString(),
                            callback
                        )
                    }
                    .setNegativeButton("Отмена") { dialog1: DialogInterface?, which: Int -> }
                    .show()
                Utils.FixDialog(dialog, applicationContext) // почему-то нужно для планшетов
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setupProgress(v: View) {
        val toggle = v as ToggleButton
        statsize = if (toggle.isChecked) 100 else 10
        readProgressData()
        val pb = findViewById<IndicatorSeekBar>(R.id.progress)
        if (statsize == 10) {
            pb.min = 0f
            pb.max = 10f
            pb.setProgress(statistics.toFloat())
            pb.customTickTexts(arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "     10"))
        } else {
            pb.min = 90f
            pb.max = 100f
            pb.customTickTexts(
                arrayOf(
                    "90    ",
                    "91",
                    "92",
                    "93",
                    "94",
                    "95",
                    "96",
                    "97",
                    "98",
                    "99",
                    "     100"
                )
            )
            pb.setProgress(if (statistics > 90) (statistics - 90) * 10 else 0.toFloat())
        }
    }

    //    void readProgressData() {
    //        int count = 0;
    //        statistics = 0;
    //        if (StatisticMaker.getTourCount(this) > 0) {
    //            for (int tourNumber = StatisticMaker.getTourCount(this) - 1; tourNumber >= 0; tourNumber--) {
    //                Tour tour = StatisticMaker.loadTour(this, tourNumber);
    //
    //                for (int i = tour.getTotalTasks() - 1; i >= 0; i--) {
    //                    Task task = tour.getTourTasks().get(i);
    //
    //                    statistics += task.correct() ? 1 : 0;
    //                    count++;
    //
    //                    if (count == statsize)
    //                        return;
    //                }
    //            }
    //        }
    //    }
    fun readProgressData() {
        var count = 0
        statistics = 0
        val tours_reversed = ArrayList(tours)
        Collections.reverse(tours_reversed)
        for (tour in tours_reversed) {
            for (i in tour.getTotalTasks() - 1 downTo 0) {
                val task: Task? = tour.getTourTasks()[i]
                statistics += if (task!!.correct()) 1 else 0
                count++
                if (count == statsize) return
            }
        }
    }

    // region chart
    protected fun setUpChart() {
        readChartData()
        val chart = LineChart(this) // = findViewById(R.id.chart);
        configureChart(chart)
        if (stat.size >= 4) {
            val data = generateDataFromArray(stat)
            chart.data = data
        } else {
            chart.setNoDataText("Недостаточно данных для постройки графика")
            chart.setNoDataTextColor(Color.WHITE)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            chart.layoutParams = params
        }
        chart.invalidate()
        // chart.animateX(750);
    }

    private fun configureChart(chart: LineChart) {
        // enable touch gestures
        chart.setTouchEnabled(true)
        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false)

//        chart.setBackgroundColor(Color.WHITE);
//        chart.setDrawGridBackground(false);
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setDrawBorders(false)
        val xAxis = chart.xAxis
        xAxis.textColor = Color.WHITE
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = (stat.size / 2).toFloat()
        xAxis.labelCount = stat.size / 2
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        val leftAxis = chart.axisLeft
        leftAxis.textColor = Color.WHITE
        leftAxis.setLabelCount(5, false)
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 100f
        leftAxis.labelCount = 10
        chart.axisRight.isEnabled = false
        val l = chart.legend
        l.isEnabled = false
    }

    fun readChartData() {
        if (StatisticMaker.getTourCount(this) > 0) {
            for (tourNumber in StatisticMaker.getTourCount(this) - 1 downTo 0) {
                val tour = StatisticMaker.loadTour(this, tourNumber)
                for (i in tour.totalTasks - 1 downTo 0) {
                    val task: Task? = tour.tourTasks[i]
                    stat.add(0, if (task!!.correct()) 1 else 0)
                    if (stat.size == 20) return
                }
            }
        }

//        if (stat.size() < 4) {
////            stat.clear();
////            CommonOperations.genereteFakeTour("10 1 1 1 1 1 0 1 1 1 1 1 1 1 1 1 0 1 0 1 1", this);
////            readChartData();
//        }
    }

    private fun generateDataFromArray(last20tasks: ArrayList<Int>): LineData {
        val values = ArrayList<Entry>()
        val pool = last20tasks.size / 2
        val border = pool - 1
        var stat = 0
        for (i in last20tasks.indices) {
            stat += last20tasks[i]
            if (i >= border) {
                stat -= last20tasks[i - border]
                values.add(
                    Entry(
                        (i - border).toFloat(), Math.round((100 * stat / pool).toFloat()).toFloat()
                    )
                )
            }
        }
        val data = LineDataSet(values, "New DataSet")
        data.lineWidth = 2.5f
        data.setDrawCircles(false)
        data.circleRadius = 0f
        data.highLightColor = Color.rgb(244, 117, 117)
        data.setDrawValues(false)
        return LineData(data)
    }

    private fun generateRandomData(): LineData {
        val values1 = ArrayList<Entry>()
        for (i in 0..99) {
            values1.add(Entry(i.toFloat(), ((Math.random() * 65).toInt() + 40).toFloat()))
        }
        val d1 = LineDataSet(values1, "New DataSet")
        d1.lineWidth = 2.5f
        d1.circleRadius = 0f
        d1.highLightColor = Color.rgb(244, 117, 117)
        d1.setDrawValues(false)

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
        val sets = ArrayList<ILineDataSet>()
        sets.add(d1)
        //sets.add(d2);
        return LineData(d1)
    } //endregion
}