package com.education4all.activities.tabs

import android.content.*
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.education4all.NotificationHelper
import com.education4all.R
import com.education4all.mathCoachAlg.DataReader
import com.education4all.utils.Enums
import com.education4all.utils.Enums.ButtonsPlace
import com.education4all.utils.Enums.TimerState
import com.education4all.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import java.text.SimpleDateFormat
import java.util.*

class SettingsAppTab : Fragment() {
    var view: View? = null
    var context: Context? = null
    private val NODISAPEARCHAR = "∞" //DecimalFormatSymbols.getInstance().getInfinity()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_app, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        context = getContext()
        view = v
        configureSpinner(
            R.id.timer_spinner,
            R.array.timer_dropdown,
            SpinnerCallback { choice: Int ->
                val options =
                    arrayOf(TimerState.CONTINIOUS, TimerState.DISCRETE, TimerState.INVISIBlE)
                val timer_state: Int = TimerState.Companion.convert(options[choice])
                DataReader.SaveInt(timer_state, DataReader.TIMER_STATE, context)
            },
            DataReader.GetInt(DataReader.TIMER_STATE, context)
        )
        configureSpinner(
            R.id.layout_spinner,
            R.array.layout_dropdown,
            SpinnerCallback { choice: Int ->
                val options = arrayOf(Enums.LayoutState._789, Enums.LayoutState._123)
                val layout_state: Int = Enums.LayoutState.Companion.convert(
                    options[choice]
                )
                DataReader.SaveInt(layout_state, DataReader.LAYOUT_STATE, context)
            },
            DataReader.GetInt(DataReader.LAYOUT_STATE, context)
        )
        configureSpinner(
            R.id.buttons_spinner,
            R.array.buttons_dropdown,
            SpinnerCallback { choice: Int ->
                val options = arrayOf(ButtonsPlace.RIGHT, ButtonsPlace.LEFT)
                val buttons_place: Int = ButtonsPlace.Companion.convert(options[choice])
                DataReader.SaveInt(buttons_place, DataReader.BUTTONS_PLACE, context)
            },
            DataReader.GetInt(DataReader.BUTTONS_PLACE, context)
        )
        configureSpinner(R.id.goal_spinner, R.array.goal_dropdown, SpinnerCallback { choice: Int ->
            val options = intArrayOf(5, 10, 15)
            val goal_minutes = options[choice]
            DataReader.SaveInt(goal_minutes, DataReader.GOAL, context)
        }, DataReader.GetInt(DataReader.GOAL, context) / 5 - 1)
        val valueToPercentage = HashMap<Int, Int>()
        valueToPercentage[1] = 0
        valueToPercentage[2] = 11
        valueToPercentage[3] = 22
        valueToPercentage[5] = 33
        valueToPercentage[10] = 44
        valueToPercentage[15] = 55
        valueToPercentage[20] = 66
        valueToPercentage[30] = 77
        valueToPercentage[45] = 88
        valueToPercentage[60] = 100
        valueToPercentage[-1] = 100
        var seekBar = view!!.findViewById<IndicatorSeekBar>(R.id.round_length_slider)
        seekBar.customTickTexts(
            arrayOf(
                "1",
                "2",
                "3",
                "5",
                "10",
                "15",
                "20",
                "30",
                "45",
                "     60"
            )
        )
        val roundTime = DataReader.GetInt(DataReader.ROUND_TIME, context)
        if (valueToPercentage.containsKey(roundTime)) seekBar.setProgress(valueToPercentage[roundTime])
        seekBar.setOnSeekChangeListener(object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                val value = seekParams.tickText.trim { it <= ' ' }
                DataReader.SaveInt(value.toInt(), DataReader.ROUND_TIME, context)
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {}
        })
        seekBar = view!!.findViewById(R.id.disappear_time_slider)
        seekBar.customTickTexts(arrayOf("1", "2", "3", "5", "10", "15", "20", "30", "45", "    ∞"))
        val disapearTime = DataReader.GetInt("DisapRoundTime", context)
        if (valueToPercentage.containsKey(disapearTime)) seekBar.setProgress(valueToPercentage[disapearTime])
        seekBar.setOnSeekChangeListener(object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                val value = seekParams.tickText.trim { it <= ' ' }
                var ivalue = 0
                ivalue = if (value == NODISAPEARCHAR) -1 else value.toInt()
                DataReader.SaveInt(ivalue, DataReader.DISAP_ROUND_TIME, context)
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {}
        })
        val id = view!!.findViewById<TextView>(R.id.id)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) id.text = user.uid
        val queue = view!!.findViewById<SwitchCompat>(R.id.queue)
        val queueEnabled = DataReader.GetBoolean(DataReader.QUEUE, context)
        queue.isChecked = queueEnabled
        if (queueEnabled) toggleQueue(v)
        queue.setOnClickListener { v: View -> toggleQueue(v) }
        val reminder = view!!.findViewById<SwitchCompat>(R.id.remind)
        val remind = DataReader.GetBoolean(DataReader.REMINDER, context)
        reminder.isChecked = remind
        if (remind) toggleReminder(v)
        reminder.setOnClickListener { v: View? -> toggleReminder(v) }
        val reminder_time = view!!.findViewById<TextView>(R.id.remind_time)
        reminder_time.text = DataReader.GetString(DataReader.REMINDER_TIME, context)
        reminder_time.setOnClickListener { v: View? -> reminderDropDown(v) }
        Log.d("dddddddddddddd", Utils.versioningTool())
        if (Utils.versioningTool() == Utils.VERSIONING_REMOVEFIREBASE) {
            val account = view!!.findViewById<TextView>(R.id.account_text)
            account.visibility = View.GONE
            view!!.findViewById<View>(R.id.id_layout).visibility = View.GONE
            view!!.findViewById<View>(R.id.queue_layout).visibility = View.GONE
            //            view.findViewById(R.id.goal_layout).setVisibility(View.GONE);
            view!!.findViewById<View>(R.id.reminder_layout).visibility = View.GONE
            view!!.findViewById<View>(R.id.remind_time_layout).visibility = View.GONE
        }
    }

    interface SpinnerCallback {
        fun onCallback(choice: Int)
    }

    private fun configureSpinner(
        spinnerId: Int,
        arrayID: Int,
        callback: SpinnerCallback,
        choice_index: Int
    ) {
        val spinner = view!!.findViewById<Spinner>(spinnerId)
        val state = resources.getStringArray(arrayID)
        val adapter = ArrayAdapter(
            context!!,
            R.layout.spinner_row, R.id.spinner_row_text, state
        )
        spinner.gravity = Gravity.END
        adapter.setDropDownViewResource(R.layout.spinner_row_unfolded)
        spinner.adapter = adapter
        spinner.setSelection(choice_index)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View,
                selectedItemPosition: Int,
                selectedId: Long
            ) {
                callback.onCallback(selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val offset =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -80f, resources.displayMetrics)
                .toInt()
        spinner.dropDownHorizontalOffset = offset
    }

    private fun toggleQueue(v: View) {
        val on = (view!!.findViewById<View>(R.id.queue) as SwitchCompat).isChecked
        DataReader.SaveBoolean(on, DataReader.QUEUE, context)
    }

    fun toggleReminder(v: View?) {
        val on = (view!!.findViewById<View>(R.id.remind) as SwitchCompat).isChecked
        if (!on) NotificationHelper(context).cancelReminder()
        val layout = view!!.findViewById<LinearLayout>(R.id.remind_time_layout)
        layout.visibility = if (on) View.VISIBLE else View.INVISIBLE
        DataReader.SaveBoolean(on, DataReader.REMINDER, context)
    }

    fun reminderDropDown(v: View?) {
        val timePicker = TimePicker.inflate(
            context,
            R.layout.time_selector, null
        ) as TimePicker
        //        timePicker.setIs24HourView(DateFormat.is24HourFormat(context));
        timePicker.setIs24HourView(true)
        val dialog = AlertDialog.Builder(
            context!!
        )
            .setTitle("Введите время напоминания").setView(timePicker)
            .setPositiveButton(
                "Ок"
            ) { dialog1: DialogInterface?, which: Int ->
                setAlarm(
                    timePicker.hour,
                    timePicker.minute
                )
            }
            .setNegativeButton("Отмена") { dialog1: DialogInterface?, which: Int -> }
            .show()
        Utils.FixDialog(dialog, context) // почему-то нужно для планшетов
    }

    private fun setAlarm(hour: Int, minute: Int) {
        val timetv = view!!.findViewById<TextView>(R.id.remind_time)
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute
        val sdf = SimpleDateFormat("HH:mm")
        val timetext = sdf.format(calendar.time)
        timetv.text = timetext
        DataReader.SaveString(timetext, DataReader.REMINDER_TIME, context)
        NotificationHelper(context).setReminder(hour, minute, NotificationHelper.Companion.MAKE)
    } //    public void themeDropdown(View view) {
    //        final TextView theme = view.findViewById(R.id.theme);
    //        final PopupMenu popup = new PopupMenu(context, theme);
    //
    //        popup.getMenuInflater().inflate(R.menu.settings_theme, popup.getMenu());
    //        popup.setOnMenuItemClickListener(item -> {
    //            int state = 0;
    //            theme.setText(item.getTitle().toString());
    //            //светлая и темная тема меняются местами, чтобы в IDE отображалась темная
    //            switch (item.getTitle().toString()) {
    //                case "Светлая":
    //                default:
    //                    state = Theme.convert(Theme.LIGHT);
    //                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    //                    break;
    //                case "Темная":
    //                    state = Theme.convert(Theme.DARK);
    //                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    //                    break;
    //                case "Системная":
    //                    state =Theme.convert(Theme.SYSTEM);
    //                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    //                    break;
    //            }
    //            DataReader.SaveInt(state, DataReader.THEME, context);
    //            return true;
    //        });
    //        popup.show();
    //    }
}