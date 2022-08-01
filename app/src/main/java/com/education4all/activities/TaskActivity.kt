package com.education4all.activities

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.format.DateUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.SuperscriptSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.gridlayout.widget.GridLayout
import com.education4all.*
import com.education4all.mathCoachAlg.DataReader
import com.education4all.mathCoachAlg.StatisticMaker
import com.education4all.mathCoachAlg.tasks.*
import com.education4all.mathCoachAlg.tours.TaskQueue
import com.education4all.mathCoachAlg.tours.Tour
import com.education4all.utils.Enums
import com.education4all.utils.Enums.ButtonsPlace
import com.education4all.utils.Enums.TimerState
import com.education4all.utils.Utils
import java.util.*

class TaskActivity : AppCompatActivity() {
    val tasktype = BuildConfig.FLAVOR
    var mode = Mode.integer
    private val progressBarHandler = Handler()
    private val taskDisapHandler = Handler()
    private val currentTour = Tour()
    private var task: Task? = null
    private var correctionwork: Task? = null
    var taskQueue: TaskQueue? = null
    private var answer: String? = ""
    private var answerI: String? = ""

    // текущая целая часть ответа
    private var answerT = ""

    // текущий числитель ответа
    private var answerB = ""

    // текущий знаменатель ответа
    private var answerF = "" // текущая дробь ответа
    private var timerText: TextView? = null
    private var progressBar: ProgressBar? = null
    private var progressStatus = 0
    private val context: Context = this
    private var answerShown = false
    private var showTask = true
    private var disapTime = 0f
    var prevTaskTime: Long = 0
    var prevAnswerTime: Long = 0
    var tourStartTime: Long = 0
    var tourLenght = 0
    var secondsFromStart = 0
    private var progressthread: Thread? = null
    private var timerthread: Thread? = null
    var solved = 0
    var shown = 1
    var answerWasShown = false

    enum class Mode {
        integer, top, bottom
    }

    var fractionSyms = arrayOf("□⁄□", "■⁄□", "□⁄■")
    var fractionSymsSpan = arrayOfNulls<SpannableString>(3)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task)
        //заполняем GridLayout
        val gl = findViewById<GridLayout>(R.id.buttonsLayout)
        fillView(gl)
        val size = resources.getDimension(R.dimen.dimen0).toInt() / 3
        for (i in fractionSyms.indices) {
            fractionSymsSpan[i] = SpannableString.valueOf(fractionSyms[i])
            fractionSymsSpan[i].setSpan(SuperscriptSpan(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            fractionSymsSpan[i].setSpan(
                AbsoluteSizeSpan(size),
                0,
                1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // fractionSymsSpan[i].setSpan(new SubscriptSpan(), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            fractionSymsSpan[i].setSpan(
                AbsoluteSizeSpan(size),
                2,
                3,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }


        //обнуляем переменные и инициализируем элементы layout
        // массив разрешённых заданий
        val allowedTasks = DataReader.readAllowedTasks(this)
        if (!Task.Companion.areTasks(allowedTasks)) {
            finish()
            startActivity(
                Intent(context, SettingsMainActivity::class.java)
                    .putExtra("FromTask", true)
            )
        } else {
            answer = ""
            handleTime()
            taskQueue = TaskQueue(allowedTasks, context)
            if (!DataReader.GetBoolean(DataReader.QUEUE, context)) taskQueue!!.setEnabled(false)
            Task.Companion.setAllowedTasks(allowedTasks)
            task = taskQueue!!.newTask()
            textViewUpdate()
        }
    }

    private fun handleTime() {
        tourStartTime = System.currentTimeMillis()
        prevTaskTime = tourStartTime
        prevAnswerTime = tourStartTime
        tourLenght = DataReader.GetInt(DataReader.ROUND_TIME, this) * 60
        val timerstate: TimerState = TimerState.Companion.convert(
            DataReader.GetInt(DataReader.TIMER_STATE, this)
        )
        progressBar = findViewById(R.id.taskProgress)
        progressBar.setProgress(0)
        progressBar.setVisibility(if (timerstate == TimerState.INVISIBlE) View.INVISIBLE else View.VISIBLE)
        progressthread = Thread(label@ Runnable {
            while (progressStatus < 100) {
                progressStatus += 1
                if (timerstate == TimerState.CONTINIOUS) progressBarHandler.post {
                    progressBar.setProgress(
                        progressStatus
                    )
                }
                try {
                    Thread.sleep(tourLenght * 10L)
                } catch (e: InterruptedException) {
                    return@label
                }
            }
        })
        timerText = findViewById(R.id.timertext)
        timerText.setVisibility(if (timerstate == TimerState.INVISIBlE) View.INVISIBLE else View.VISIBLE)
        timerText.setText(timeString(secondsFromStart, tourLenght))
        timerthread = Thread(label@ Runnable {
            while (secondsFromStart < tourLenght) {
                secondsFromStart += 1
                if (timerstate == TimerState.CONTINIOUS) progressBarHandler.post {
                    timerText.setText(
                        timeString(secondsFromStart, tourLenght)
                    )
                }
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    return@label
                }
            }
        })

//        if (timerstate == Enums.TimerState.CONTINIOUS) {
        timerthread!!.start()
        progressthread!!.start()
        //        }
        disapTime = DataReader.GetInt(DataReader.DISAP_ROUND_TIME, this).toFloat()
        resetDissapearing()
    }

    // формирует вывод о прошедшем времени
    private fun timeString(count: Int, max: Int): String {
        return String.format(
            Locale.getDefault(), "%s/%s",
            DateUtils.formatElapsedTime(count.toLong()),
            DateUtils.formatElapsedTime(max.toLong())
        )
    }

    //обновляет текстовый и визуальный таймеры
    fun updateTimers() {
        progressBar!!.progress = progressStatus
        timerText!!.text = timeString(secondsFromStart, tourLenght)
    }

    //делаем все кнопки одинакового размера внутри GridLayout
    @SuppressLint("ClickableViewAccessibility")
    fun fillView(parent: GridLayout) {
        //Button child;
        val sym: Button
        when (tasktype) {
            "integers" -> {
                sym = findViewById(R.id.Button_sym)
                sym.text = ""
                sym.tag = "empty"
                sym.setOnClickListener(null)
                sym.setOnLongClickListener(null)
                sym.setOnTouchListener(null)
                val linesym = findViewById<View>(R.id.Line_sym)
                linesym.setBackgroundColor(Color.TRANSPARENT)
            }
            "fractions" -> {
                sym = findViewById(R.id.Button_sym)
                sym.tag = "/"
                setModeSym()
            }
            "decimals" -> {}
            else -> {}
        }
        for (i in 0 until parent.childCount) {
            val childLayout = parent.getChildAt(i) as RelativeLayout
            val child = childLayout.getChildAt(0)
            val line = childLayout.getChildAt(1)
            if (child.javaClass == AppCompatButton::class.java && child.tag != "empty") {
                if (child.tag == "del") child.setOnLongClickListener { v: View? ->
                    answer = ""
                    textViewUpdate()
                    true
                } else child.setOnLongClickListener { v: View ->
                    numberPress(v)
                    true
                }
                child.setOnTouchListener { v: View, event: MotionEvent ->
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_DOWN -> line.setBackgroundColor(
                            resources.getColor(R.color.main)
                        )
                        MotionEvent.ACTION_UP -> {
                            line.setBackgroundColor(resources.getColor(R.color.shadowed))
                            v.performClick()
                        }
                        else -> {}
                    }
                    true
                }
            }
        }
        val buttonsPlace: ButtonsPlace = ButtonsPlace.Companion.convert(
            DataReader.GetInt(DataReader.BUTTONS_PLACE, this)
        )
        val layoutState: Enums.LayoutState = Enums.LayoutState.Companion.convert(
            DataReader.GetInt(DataReader.LAYOUT_STATE, this)
        )
        if (buttonsPlace == ButtonsPlace.LEFT) alterActionSides()
        if (layoutState == Enums.LayoutState._123) alterLayout()
    }

    //меняет раскладку с 789 на 123
    fun alterLayout() {
        switchPlaces(
            findViewById(R.id.Layout_7),
            findViewById(R.id.Layout_1)
        )
        switchPlaces(
            findViewById(R.id.Layout_8),
            findViewById(R.id.Layout_2)
        )
        switchPlaces(
            findViewById(R.id.Layout_9),
            findViewById(R.id.Layout_3)
        )
    }

    //меняет местами два объекта
    fun switchPlaces(b1: View, b2: View) {
        val temp = b1.layoutParams
        b1.layoutParams = b2.layoutParams
        b2.layoutParams = temp
    }

    //перемещает кнопки-значки в левую сторону экрана
    fun alterActionSides() {
        var imagebutton = findViewById<View>(R.id.Layout_Del)
        switchPlaces(findViewById(R.id.Layout_9), imagebutton)
        switchPlaces(findViewById(R.id.Layout_8), imagebutton)
        switchPlaces(findViewById(R.id.Layout_7), imagebutton)
        imagebutton = findViewById(R.id.Layout_skip)
        switchPlaces(findViewById(R.id.Layout_6), imagebutton)
        switchPlaces(findViewById(R.id.Layout_5), imagebutton)
        switchPlaces(findViewById(R.id.Layout_4), imagebutton)
        imagebutton = findViewById(R.id.Layout_help)
        switchPlaces(findViewById(R.id.Layout_3), imagebutton)
        switchPlaces(findViewById(R.id.Layout_2), imagebutton)
        switchPlaces(findViewById(R.id.Layout_1), imagebutton)
        imagebutton = findViewById(R.id.Layout_check)
        switchPlaces(findViewById(R.id.Layout_sym), imagebutton)
        switchPlaces(findViewById(R.id.Layout_0), imagebutton)
        switchPlaces(findViewById(R.id.Layout_empty), imagebutton)
    }

    override fun onBackPressed() {
        crossClick(findViewById(R.id.cross))
    }

    //сохраняем информацию о данной попытке и обновляем информацию раунда
    private fun saveTaskStatistic(finished: Boolean) {
        task!!.makeUserAnswer(
            answer,
            ((System.currentTimeMillis() - prevAnswerTime) / 1000).toString()
        )
        prevAnswerTime = System.currentTimeMillis()
        if (finished) {
            task!!.timeTaken = (System.currentTimeMillis() - prevTaskTime) / 1000
            currentTour.addTask(task)
            prevTaskTime = prevAnswerTime
        } else {
            currentTour.addTask(task)
            task = Task.Companion.makeTask(task)
        }

        //обновляем реальную продолжительность раунда
        currentTour.tourTime =
            (Calendar.getInstance().timeInMillis - currentTour.tourDateTime) / 1000
    }

    //нажатие на кнопку "ОК", проверяем правильность ответа и заносим в статистику
    fun okButtonClick(view: View?) {
        resetMode()
        if (answer == "") return
        resetDissapearing()
        updateTimers()
        if (answerShown) {
            answerI = "?"
            answerT = ""
            answerB = ""
            answerF = ""
            answer = "?"
            answerShown = false
        }
        if (answer == task.getAnswer()) {
            updateProgressIcons(getString(R.string.star))
            startNewTask()
        } else {
            saveTaskStatistic(false)
            if (answer != "?") {
                saveCorrectionWork()
                updateWrongAnswers(answer)
                updateProgressIcons(getString(R.string.dot))
                answerWasShown = false
            }
            answerI = ""
            answerT = ""
            answerB = ""
            answerF = ""
            answer = ""
            textViewUpdate()
        }
    }

    //выводит новый неправильный ответ к остальным
    private fun updateWrongAnswers(answer: String?) {
        val wrongAnswers = findViewById<TextView>(R.id.wrongAnswers)
        val text = StringBuilder()
        val line = wrongAnswers.text.toString()
        if (!line.isEmpty()) {
            val answers = line.split(", ").toTypedArray()
            for (s in answers) text.append(
                String.format(
                    Locale.getDefault(),
                    "<strike>%s</strike>, ",
                    s
                )
            )
        }
        text.append(String.format(Locale.getDefault(), "<strike>%s</strike>", answer))
        wrongAnswers.text = Html.fromHtml(text.toString())
    }

    //пропуск задания
    fun skipTask(view: View?) {
        resetDissapearing()
        updateTimers()
        if (answerShown) {
            answer = "?"
        } else {
            answer = "\u2026"
            updateProgressIcons(getString(R.string.arrow))
        }
        startNewTask()
    }

    //запуск нового задания
    private fun startNewTask() {
        saveTaskStatistic(true)
        task = taskQueue!!.newTask()
        correctionwork = null
        answerShown = false
        answerWasShown = false
        answer = ""
        answerI = ""
        answerT = ""
        answerB = ""
        answerF = ""
        resetMode()
        val wrongAnswers = findViewById<TextView>(R.id.wrongAnswers)
        wrongAnswers.text = ""
        updateTimers()
        if ((System.currentTimeMillis() - tourStartTime) / 1000 >= tourLenght) endRound() else textViewUpdate()
    }

    //добавляем иконку в зависимости от статуса задания
    private fun updateProgressIcons(icon: String) {
        val pi = findViewById<TextView>(R.id.progressIcons)
        var text = pi.text.toString()
        text += icon
        pi.text = text
        shown++
        if (icon == getString(R.string.star)) solved++
        val statistics = findViewById<TextView>(R.id.statistics)
        statistics.text = String.format(
            Locale.getDefault(), "Решено %d/%d (%d%%)",
            solved, shown - 1, solved * 100 / (shown - 1)
        )
    }

    //завершение раунда
    private fun endRound() {
        if (answerShown) {
            answer = "?"
            saveTaskStatistic(true)
        }
        StatisticMaker.saveTour(currentTour, context)
        Log.d("debuggggggggggggggggg", "2222222")
        taskQueue!!.save()
        val builder = AlertDialog.Builder(this) //, R.style.AlertDialogTheme)
            .setTitle("Раунд завершён")
            .setMessage(
                String.format(
                    Locale.getDefault(), "Решено заданий: %d/%d (%d%%)",
                    currentTour.rightTasks,
                    currentTour.totalTasks,
                    100 * currentTour.rightTasks / currentTour.totalTasks
                )
            )
            .setCancelable(false)
        val inflater = layoutInflater
        // Pass null as the parent view because its going in the dialog layout
        val view = inflater.inflate(R.layout.dialog_endround, null)
        val donate = view.findViewById<TextView>(R.id.donate)
        val ss = SpannableString(getString(R.string.donatetext))
        ss.setSpan(
            URLSpan(getString(R.string.donatelink)),
            0,
            ss.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        donate.text = ss
        builder.setView(view)
            .setNeutralButton("Ещё раунд") { dialog: DialogInterface?, which: Int ->
                finish()
                startActivity(intent)
            }
            .setNegativeButton("Подробнее") { dialog: DialogInterface?, which: Int ->
                finish()
                val tourCount = StatisticMaker.getTourCount(context)
                val i = Intent(context, StatTourActivity::class.java)
                i.putExtra("Tour", (tourCount - 1))
                startActivity(i)
            }
            .setPositiveButton("В начало") { dialog: DialogInterface?, which: Int -> finish() }
        val dialog = builder.create()
        dialog.setOnKeyListener { arg0: DialogInterface?, keyCode: Int, event: KeyEvent? ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).callOnClick()
            }
            true
        }
        dialog.show()
        Utils.FixDialog(dialog, context) // почему-то нужно для планшетов
    }

    fun goToWeb(view: View?) {
        val intent = Intent(this, WebActivity::class.java)
        startActivity(intent)
        finish()
    }

    //досрочное завершение раунда по нажатию выхода
    fun crossClick(view: View?) {
        if (currentTour.totalTasks == 0 && task.getUserAnswer().length == 0 && !answerShown) {
            finish()
            return
        }
        val dialog = AlertDialog.Builder(this) //, R.style.AlertDialogTheme)
            .setTitle("Досрочное завершение раунда")
            .setMessage("Сохранить результаты?")
            .setNeutralButton("Отмена") { dialog1: DialogInterface?, which: Int -> }
            .setNegativeButton("Нет") { dialog12: DialogInterface?, which: Int ->
                taskQueue!!.save()
                finish()
            }
            .setPositiveButton("Да") { dialog13: DialogInterface?, which: Int -> endRound() }.show()
        Utils.FixDialog(dialog, context) // почему-то нужно для планшетов
    }

    //обновляем поля вывода выражения
    private fun textViewUpdate() {
        val expressionTV = findViewById<TextView>(R.id.expTextView)
        if (tasktype == "fractions") {
            answerF = Fraction.Companion.makeFraction(answerT, answerB)
            if (answerF.isEmpty() && mode == Mode.top) answerF = " ⁄"
            answer = answerI + answerF
        }
        if (showTask) expressionTV.text =
            task.getExpression() + " = " + answer else expressionTV.text =
            getString(R.string.threedots) + " = " + answer
    }

    //нажатие на цифровую кнопку
    fun numberPress(view: View) {
        if (answerShown && answer!!.length > 0) {
            return
        }
        val symbol = view.tag.toString()
        if (answer == "0" && symbol != ",") {
            answer = ""
            textViewUpdate()
        } else if (symbol == "," && (answer!!.contains(",") || answer!!.isEmpty())) return
        if (symbol == "/") {
            switchMode(true)
            textViewUpdate()
            return
        }
        if (tasktype != "fractions") answer += symbol else when (mode) {
            Mode.integer -> if (!(symbol == "0" && answerI!!.isEmpty())) answerI += symbol
            Mode.top -> if (!(symbol == "0" && answerT.isEmpty())) answerT += symbol
            Mode.bottom -> if (!(symbol == "0" && answerB.isEmpty())) answerB += symbol
        }
        textViewUpdate()
    }

    fun switchMode(forward: Boolean) {
        if (forward) when (mode) {
            Mode.integer -> mode = Mode.top
            Mode.top -> if (!answerT.isEmpty()) mode = Mode.bottom
            Mode.bottom -> {}
            else -> {}
        } else when (mode) {
            Mode.top -> mode = Mode.integer
            Mode.bottom -> mode = Mode.top
            Mode.integer -> {}
            else -> {}
        }
        setModeSym()
    }

    private fun setModeSym() {
        (findViewById<View>(R.id.Button_sym) as Button).setText(
            fractionSymsSpan[mode.ordinal],
            TextView.BufferType.SPANNABLE
        )
    }

    fun resetMode() {
        if (tasktype == "fractions") {
            mode = Mode.integer
            setModeSym()
        }
    }

    //удаление одного символа
    fun charDelete(view: View?) {
        if (!answerShown) {
            if (answer!!.length > 0 || tasktype == "fractions") { //(answerI + answerT + answerB).length() > 0) {
                if (tasktype != "fractions") answer =
                    answer!!.substring(0, answer!!.length - 1) else when (mode) {
                    Mode.bottom -> {
                        if (!answerB.isEmpty()) {
                            answerB = answerB.substring(0, answerB.length - 1)
                            break
                        } else switchMode(false)
                        if (!answerT.isEmpty()) {
                            answerT = answerT.substring(0, answerT.length - 1)
                            break
                        } else switchMode(false)
                        if (!answerI!!.isEmpty()) answerI =
                            answerI!!.substring(0, answerI!!.length - 1)
                    }
                    Mode.top -> {
                        if (!answerT.isEmpty()) {
                            answerT = answerT.substring(0, answerT.length - 1)
                            break
                        } else switchMode(false)
                        if (!answerI!!.isEmpty()) answerI =
                            answerI!!.substring(0, answerI!!.length - 1)
                    }
                    Mode.integer -> if (!answerI!!.isEmpty()) answerI =
                        answerI!!.substring(0, answerI!!.length - 1)
                    else -> if (!answerI!!.isEmpty()) answerI =
                        answerI!!.substring(0, answerI!!.length - 1)
                }
                textViewUpdate()
            }
        }
    }

    //показать ответ
    fun showAnswer(view: View?) {
        resetMode()
        answer = task.getAnswer()
        answerI = task.getAnswer()
        answerF = ""
        answerShown = true
        if (!answerWasShown) updateProgressIcons("?")
        answerWasShown = true
        textViewUpdate()
        saveCorrectionWork()
    }

    fun saveCorrectionWork() {
        if (correctionwork == null) {
            correctionwork = task
            taskQueue!!.Add(task)
        }
    }

    //таймер для исчезновения задания
    private val disapTask = Runnable {
        showTask = false
        textViewUpdate()
        val pressToShowTaskTV = findViewById<TextView>(R.id.pressToShowTaskTV)
        //pressToShowTaskTV.setText("Нажмите, чтобы показать задание");
        pressToShowTaskTV.visibility = View.VISIBLE
        val pi = findViewById<TextView>(R.id.progressIcons)
        pi.visibility = View.INVISIBLE
    }

    //Таймер перезапускается в 4-х случаях:
    //1) При первичной инициализации, 2) при нажатии на ОК, 3) при нажатии на пропуск задания, 4) при нажатии на showTask.
    //Не перезапускается при цифровых кнопках, DEL, показывании ответа
    //При перезапуске таймера необходимо показать задание. Это нужно делать перед textViewUpdate.
    private fun resetDissapearing() {
        showTask = true
        val pressToShowTaskTV = findViewById<TextView>(R.id.pressToShowTaskTV)
        //pressToShowTaskTV.setText("Нажмите, чтобы показать задание");
        pressToShowTaskTV.visibility = View.INVISIBLE
        val pi = findViewById<TextView>(R.id.progressIcons)
        pi.visibility = View.VISIBLE
        taskDisapHandler.removeCallbacks(disapTask)
        if (disapTime > -1) {
            taskDisapHandler.postDelayed(disapTask, (disapTime * 1000).toLong())
        }
    }

    //просмотр задания, если оно исчезло
    fun showTask(view: View?) {
        resetDissapearing()
        val pressToShowTaskTV = findViewById<TextView>(R.id.pressToShowTaskTV)
        //pressToShowTaskTV.setText("");
        pressToShowTaskTV.visibility = View.INVISIBLE
        textViewUpdate()
    }

    //при выходе из активности необходимо остановить все таймеры
    override fun onDestroy() {
        taskDisapHandler.removeCallbacks(disapTask)
        if (progressthread != null) progressthread!!.interrupt()
        if (timerthread != null) timerthread!!.interrupt()
        super.onDestroy()
    }
}