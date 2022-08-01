package com.education4all.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.education4all.*
import com.education4all.firebase.FireBaseUtils
import com.education4all.mathCoachAlg.DataReader
import com.education4all.mathCoachAlg.StatisticMaker
import com.education4all.mathCoachAlg.tasks.*
import com.education4all.utils.Enums
import com.education4all.utils.Enums.twodates
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val tasktype = BuildConfig.FLAVOR
    var fireBaseUtils: FireBaseUtils? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setTitle("")
        Task.Companion.setType(tasktype)
        fireBaseUtils = FireBaseUtils()
        fireBaseUtils!!.login(this) {
            try {
                showStatitics()
            } catch (e: Exception) {
                val statistics = findViewById<TextView>(R.id.statistics)
                statistics.text =
                    "\nИзменен формат записи результатов\nПерейдите на экран результатов для обновления"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showSettings()
        disableButtons(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            val intent = Intent(this, AuthorsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)

//        case R.id.action_update:
//        CommonOperations.updateStatistics(this);
//        break;
//        case R.id.action_trash:
//        StatisticMaker.removeStatistics(this);
//        break;
//        case R.id.action_generate:
//        CommonOperations.genereteFakeStatistics(this, false);
//        break;
//        case R.id.action_generateOLD:
//        CommonOperations.genereteFakeStatistics(this, true);
//        break;
//        case R.id.action_save:
//        CommonOperations.save(this);
//        break;
//        case R.id.action_load:
//        CommonOperations.load(this);
//        break;
    }

    private fun disableButtons(enabled: Boolean) {
        findViewById<View>(R.id.stat_button).isEnabled = enabled
        findViewById<View>(R.id.settings_button).isEnabled = enabled
        findViewById<View>(R.id.start_button).isEnabled = enabled
    }

    fun startTasks(view: View?) {
        disableButtons(false)
        val intent = Intent(this, TaskActivity::class.java)
        startActivity(intent)
    }

    fun startSettings(view: View?) {
        disableButtons(false)
        val intent = Intent(this, SettingsMainActivity::class.java)
        startActivity(intent)
    }

    fun startStatistic(view: View?) {
        disableButtons(false)
        val intent = Intent(this, StatiscticsActivity::class.java)
        // Intent intent = new Intent(this, MultiLineChartActivity.class);
        startActivity(intent)
    }

    @Throws(Exception::class)
    private fun showStatitics() {
        val statistics = findViewById<TextView>(R.id.statistics)
        var solvedText: String? = "Сейчас 0, в среднем 0"
        var daysText: String? = "Сейчас 0, в среднем 0"
        var secondstoday = 0
        val goal = DataReader.GetInt(DataReader.GOAL, this) * 60
        if (StatisticMaker.getTourCount(this) > 0) {
            var subsequentAnswers = 0
            var subsequentDays = 0
            var lastAnswerStreakIsValid = false
            var lastDayStreakIsvalid = false
            val streakDays = ArrayList<Int>()
            val streakSolved = ArrayList<Int>()
            var date: String? = null
            var prevdate: String? = null
            var lastStreakEndDate: String? = null
            for (tourNumber in 0 until StatisticMaker.getTourCount(this)) {
                val tour = StatisticMaker.loadTour(this, tourNumber)
                if (tour.totalTasks == 0) throw Exception("Старый формат данных")
                val seconds = tour.tourTime.toInt()
                date = tour!!.date()
                var relationOfDates: twodates? = twodates.equal
                if (tourNumber > 0) relationOfDates = Enums.isSubsequent(prevdate, date)
                if (relationOfDates == twodates.equal) secondstoday += seconds else {
                    if (secondstoday >= goal) subsequentDays++
                    secondstoday = seconds
                    if (relationOfDates != twodates.subsequent && subsequentDays > 0) {
                        streakDays.add(subsequentDays)
                        lastStreakEndDate = prevdate
                        subsequentDays = 0
                    }
                }
                for (task in tour.tourTasks) {
                    val right = task!!.correct()
                    if (right) {
                        subsequentAnswers++
                        lastAnswerStreakIsValid = true
                    } else {
                        if (subsequentAnswers != 0) {
                            streakSolved.add(subsequentAnswers)
                            subsequentAnswers = 0
                            lastAnswerStreakIsValid = false
                        }
                    }
                }
                prevdate = date
            }
            if (secondstoday >= goal) subsequentDays++
            if (subsequentDays != 0) {
                streakDays.add(subsequentDays)
                lastStreakEndDate = date
            }
            if (subsequentAnswers != 0) streakSolved.add(subsequentAnswers)
            val sdf = SimpleDateFormat("dd.MM.yyyy")
            val c = Calendar.getInstance()
            c.time = Date()
            val today = sdf.format(c.time)
            if (streakDays.size > 0) if (Enums.isSubsequent(
                    lastStreakEndDate,
                    today
                ) != twodates.unrelated
            ) lastDayStreakIsvalid = true
            if (Enums.isSubsequent(date, today) != twodates.equal) secondstoday = 0
            var averageDays = 0
            var averageSolved = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                averageDays = Math.round(streakDays.stream().mapToInt { `val`: Int? -> `val`!! }
                    .average().orElse(0.0)).toInt()
                averageSolved = Math.round(streakSolved.stream().mapToInt { `val`: Int? -> `val`!! }
                    .average().orElse(0.0)).toInt()
            } else {
                if (!streakDays.isEmpty()) {
                    for (streak in streakDays) averageDays += streak
                    averageDays = Math.round(averageDays.toDouble() / streakDays.size).toInt()
                }
                if (!streakSolved.isEmpty()) {
                    for (streak in streakSolved) averageSolved += streak
                    averageSolved = Math.round(averageSolved.toDouble() / streakSolved.size).toInt()
                }
            }
            if (streakSolved.size > 0) solvedText = String.format(
                "Сейчас %d, в среднем %d",
                if (lastAnswerStreakIsValid) streakSolved[streakSolved.size - 1] else 0,
                averageSolved
            )
            if (streakDays.size > 0) daysText = String.format(
                "Сейчас %d, в среднем %d",
                if (lastDayStreakIsvalid) streakDays[streakDays.size - 1] else 0, averageDays
            )
        }
        statistics.text = String.format(
            """
    Решено подряд:
    %s
    Дней подряд:
    %s
    """.trimIndent(),
            solvedText, daysText
        )
        showGoal(secondstoday, goal)
    }

    private fun showSettings() {
        val settings = findViewById<TextView>(R.id.settings_text)
        var text = ""

//        String[] operations = Task.getOperations();
//
//        for (int action = 0; action < 4; action++) {
//            String fragment = "";
//            if (BuildConfig.FLAVOR.equals("integers") && action == 3) {
//                for (int i = 0; i <= 3; ++i)
//                    if (i == 1) {
//                        if (DataReader.checkComplexity(3, 1, this) ||
//                                DataReader.checkComplexity(3, 4, this))
//                            fragment += 2 + ", ";
//                    } else if (DataReader.checkComplexity(action, i, this))
//                        fragment += (i + 1) + ", ";
//            } else {
//                for (int i = 0; i <= 3; ++i)
//                    if (DataReader.checkComplexity(action, i, this))
//                        fragment += (i + 1) + ", ";
//            }
//
//            if (!fragment.isEmpty()) {
//                fragment = fragment.substring(0, fragment.lastIndexOf(","));
//                if (action < 3)
//                    fragment += " ";
//
//                text += operations[action] + " " + fragment;
//            }
//        }
//
//        if (text.isEmpty())
//            text = "Задания не выбраны";
//
//        text += "\n";
        text += String.format(
            "Длина раунда %d мин\n",
            DataReader.GetInt(DataReader.ROUND_TIME, this)
        )
        val disapTime = DataReader.GetInt(DataReader.DISAP_ROUND_TIME, this)
        text += if (disapTime != -1) String.format(
            "Условие исчезает через %d сек",
            disapTime
        ) else "Условие не исчезает"
        settings.text = text
    }

    private fun showGoal(secondstoday: Int, goal: Int) {
        val goaltext = findViewById<TextView>(R.id.goal_text)
        goaltext.text = String.format(
            "Цель: %s/%s",
            DateUtils.formatElapsedTime(secondstoday.toLong()),
            DateUtils.formatElapsedTime(goal.toLong())
        )
    }
}