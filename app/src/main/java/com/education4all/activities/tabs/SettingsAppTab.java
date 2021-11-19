package com.education4all.activities.tabs;

import static com.education4all.utils.Utils.versioningTool;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.education4all.BuildConfig;
import com.education4all.NotificationHelper;
import com.education4all.R;
import com.education4all.firebase.FireBaseUtils;
import com.education4all.mathCoachAlg.DataReader;
import com.education4all.mathCoachAlg.tours.Tour;
import com.education4all.utils.Enums.ButtonsPlace;
import com.education4all.utils.Enums.LayoutState;
import com.education4all.utils.Enums.TimerState;
import com.education4all.utils.Utils;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SettingsAppTab extends Fragment {
    View view;
    Context context;
    private final String NODISAPEARCHAR = "∞"; //DecimalFormatSymbols.getInstance().getInfinity()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_app, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        context = getContext();
        view = v;
        configureSpinner(R.id.timer_spinner, R.array.timer_dropdown, (int choice) -> {
            TimerState[] options = new TimerState[]{TimerState.CONTINIOUS, TimerState.DISCRETE, TimerState.INVISIBlE};
            int timer_state = TimerState.convert(options[choice]);
            DataReader.SaveInt(timer_state, DataReader.TIMER_STATE, context);
        }, DataReader.GetInt(DataReader.TIMER_STATE, context));

        configureSpinner(R.id.layout_spinner, R.array.layout_dropdown, (int choice) -> {
            LayoutState[] options = new LayoutState[]{LayoutState._123, LayoutState._789};
            int layout_state = LayoutState.convert(options[choice]);
            DataReader.SaveInt(layout_state, DataReader.LAYOUT_STATE, context);
        }, DataReader.GetInt(DataReader.LAYOUT_STATE, context));

        configureSpinner(R.id.buttons_spinner, R.array.buttons_dropdown, (int choice) -> {
            ButtonsPlace[] options = new ButtonsPlace[]{ButtonsPlace.RIGHT, ButtonsPlace.LEFT};
            int buttons_place = ButtonsPlace.convert(options[choice]);
            DataReader.SaveInt(buttons_place, DataReader.BUTTONS_PLACE, context);
        }, DataReader.GetInt(DataReader.BUTTONS_PLACE, context));

        configureSpinner(R.id.goal_spinner, R.array.goal_dropdown, (int choice) -> {
            int[] options = new int[]{5, 10, 15};
            int goal_minutes = options[choice];
            DataReader.SaveInt(goal_minutes, DataReader.GOAL, context);
        }, DataReader.GetInt(DataReader.GOAL, context) / 5 - 1);

        HashMap<Integer, Integer> valueToPercentage = new HashMap<>();
        valueToPercentage.put(1, 0);
        valueToPercentage.put(2, 11);
        valueToPercentage.put(3, 22);
        valueToPercentage.put(5, 33);
        valueToPercentage.put(10, 44);
        valueToPercentage.put(15, 55);
        valueToPercentage.put(20, 66);
        valueToPercentage.put(30, 77);
        valueToPercentage.put(45, 88);
        valueToPercentage.put(60, 100);
        valueToPercentage.put(-1, 100);

        IndicatorSeekBar seekBar = view.findViewById(R.id.round_length_slider);
        seekBar.customTickTexts(new String[]{"1", "2", "3", "5", "10", "15", "20", "30", "45", "     60"});
        int roundTime = DataReader.GetInt(DataReader.ROUND_TIME, context);
        if (valueToPercentage.containsKey(roundTime))
            seekBar.setProgress(valueToPercentage.get(roundTime));

        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                String value = seekParams.tickText.trim();
                DataReader.SaveInt(Integer.parseInt(value), DataReader.ROUND_TIME, context);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            }
        });


        seekBar = view.findViewById(R.id.disappear_time_slider);
        seekBar.customTickTexts(new String[]{"1", "2", "3", "5", "10", "15", "20", "30", "45", "    ∞"});
        int disapearTime = DataReader.GetInt("DisapRoundTime", context);
        if (valueToPercentage.containsKey(disapearTime))
            seekBar.setProgress(valueToPercentage.get(disapearTime));

        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                String value = seekParams.tickText.trim();
                int ivalue = 0;
                if (value.equals(NODISAPEARCHAR))
                    ivalue = -1;
                else
                    ivalue = Integer.parseInt(value);
                DataReader.SaveInt(ivalue, DataReader.DISAP_ROUND_TIME, context);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            }
        });

        TextView id = view.findViewById(R.id.id);
        if (FireBaseUtils.getUser() != null)
            id.setText(FireBaseUtils.getUser().getUid());
        else
            FireBaseUtils.login(context, id::setText);

        SwitchCompat queue = view.findViewById(R.id.queue);
        boolean queueEnabled = DataReader.GetBoolean(DataReader.QUEUE, context);
        queue.setChecked(queueEnabled);
        if (queueEnabled)
            toggleQueue(v);
        queue.setOnClickListener(this::toggleQueue);

        SwitchCompat reminder = view.findViewById(R.id.remind);
        boolean remind = DataReader.GetBoolean(DataReader.REMINDER, context);
        reminder.setChecked(remind);
        if (remind)
            toggleReminder(v);
        reminder.setOnClickListener(this::toggleReminder);

        TextView reminder_time = view.findViewById(R.id.remind_time);
        reminder_time.setText(DataReader.GetString(DataReader.REMINDER_TIME, context));
        reminder_time.setOnClickListener(this::reminderDropDown);

        if (versioningTool().equals("decimalsBeta")) {
            TextView account = view.findViewById(R.id.account_text);
            account.setVisibility(View.GONE);
            view.findViewById(R.id.id_layout).setVisibility(View.GONE);
            view.findViewById(R.id.queue_layout).setVisibility(View.GONE);
//            view.findViewById(R.id.goal_layout).setVisibility(View.GONE);
            view.findViewById(R.id.reminder_layout).setVisibility(View.GONE);
            view.findViewById(R.id.remind_time_layout).setVisibility(View.GONE);
        }
    }


    public interface SpinnerCallback {
        void onCallback(int choice);
    }

    private void configureSpinner(int spinnerId, int arrayID, SpinnerCallback callback, int choice_index) {
        Spinner spinner = view.findViewById(spinnerId);
        String[] state = getResources().getStringArray(arrayID);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.spinner_row, R.id.spinner_row_text, state);
        spinner.setGravity(Gravity.END);
        adapter.setDropDownViewResource(R.layout.spinner_row_unfolded);

        spinner.setAdapter(adapter);
        spinner.setSelection(choice_index);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected,
                                       int selectedItemPosition,
                                       long selectedId) {
                callback.onCallback(selectedItemPosition);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -80, getResources().getDisplayMetrics());
        spinner.setDropDownHorizontalOffset(offset);

    }

    private void toggleQueue(View v) {
        boolean on = ((SwitchCompat) view.findViewById(R.id.queue)).isChecked();
        DataReader.SaveBoolean(on, DataReader.QUEUE, context);
    }


    public void toggleReminder(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean on = ((SwitchCompat) view.findViewById(R.id.remind)).isChecked();
            if (!on)
                new NotificationHelper(context).cancelReminder();

            LinearLayout layout = view.findViewById(R.id.remind_time_layout);
            layout.setVisibility(on ? View.VISIBLE : View.INVISIBLE);

            DataReader.SaveBoolean(on, DataReader.REMINDER, context);
        } else
            Toast.makeText(context,
                    "Напоминания не поддерживаются в этой верссии android",
                    Toast.LENGTH_LONG)
                    .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void reminderDropDown(View v) {
        TimePicker timePicker = (TimePicker) TimePicker.inflate(context,
                R.layout.time_selector, null);
//        timePicker.setIs24HourView(DateFormat.is24HourFormat(context));
        timePicker.setIs24HourView(true);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Введите время напоминания").setView(timePicker)
                .setPositiveButton("Ок", (dialog1, which) ->
                        setAlarm(timePicker.getHour(), timePicker.getMinute())
                ).setNegativeButton("Отмена", (dialog1, which) -> {
                })
                .show();
        Utils.FixDialog(dialog, context); // почему-то нужно для планшетов
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm(int hour, int minute) {
        TextView timetv = view.findViewById(R.id.remind_time);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String timetext = sdf.format(calendar.getTime());
        timetv.setText(timetext);
        DataReader.SaveString(timetext, DataReader.REMINDER_TIME, context);

        new NotificationHelper(context).setReminder(hour, minute, NotificationHelper.MAKE);
    }


//    public void themeDropdown(View view) {
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