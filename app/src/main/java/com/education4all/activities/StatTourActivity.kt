package com.education4all.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.education4all.R
import com.education4all.firebase.FireBaseUtils
import com.education4all.mathCoachAlg.StatisticMaker
import com.education4all.utils.Utils

class StatTourActivity : AppCompatActivity() {
    var TourNumber = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stat_tour)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        // myToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_trash));
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        TourNumber = intent.getIntExtra("Tour", -1)
        if (TourNumber >= 0) {
            val layout = findViewById<LinearLayout>(R.id.linear)
            val tour = StatisticMaker.loadTour(this, TourNumber)
            val line = String.format(
                "%s %d/%d (%d%%)",
                getString(R.string.star),
                tour.rightTasks,
                tour.totalTasks,
                tour.rightTasks * 100 / tour.totalTasks
            )
            supportActionBar!!.title = line
            for (task in tour.tourTasks) {
                val row = LinearLayout(this)
                LinearLayout.inflate(this, R.layout.stat_tour_block, row)
                val taskview = row.findViewById<TextView>(R.id.task)
                val seconds = row.findViewById<TextView>(R.id.seconds)
                val taskAndUserAnswer = task.expression + " = " + task.userAnswer
                taskview.text = taskAndUserAnswer
                val userAnswerIsCorrect = task!!.correct()
                if (userAnswerIsCorrect) taskview.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.main
                    )
                ) else taskview.setTextColor(ContextCompat.getColor(this, R.color.shadowed))
                val userTime = task.userAnswerTime + " сек."
                seconds.text = userTime
                layout.addView(row)
            }
        } else finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_stat_tour, menu)
        return true
    }

    private fun deleteTour() {
        StatisticMaker.removeTour(this, TourNumber)
        //        Intent intent = new Intent(this, StatiscticsActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish()
        FireBaseUtils().updateTours(applicationContext)
        //startActivity(intent);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_delete_tour) {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Удаление результатов")
                .setMessage("Вы уверены? Это действие нельзя будет отменить.")
                .setNegativeButton("Отменить") { dialog1: DialogInterface?, which: Int -> }
                .setPositiveButton("Удалить") { dialog12: DialogInterface?, which: Int -> deleteTour() }
                .show()
            Utils.FixDialog(dialog, applicationContext) // почему-то нужно для планшетов
            true
        } else super.onOptionsItemSelected(item)
    }
}