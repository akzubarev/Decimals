package com.education4all.activities.tabs;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.education4all.utils.Enums.ButtonsPlace;
import com.education4all.utils.Enums.LayoutState;
import com.education4all.utils.Enums.TimerState;
import com.education4all.utils.Utils;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        String state = "";

        TextView timertv = view.findViewById(R.id.TimerState);
        TimerState timerState = TimerState.convert(
                DataReader.GetInt(DataReader.TIMER_STATE, context)
        );
        switch (timerState) {
            case CONTINIOUS:
                state = "Непрерывный";
                break;
            case DISCRETE:
                state = "По шагам";
                break;
            case INVISIBlE:
                state = "Нет";
                break;
        }
        timertv.setText(state);
        timertv.setOnClickListener(this::timerDropdown);

        TextView numberstv = view.findViewById(R.id.numbersLayout);
        LayoutState layoutState = LayoutState.convert(
                DataReader.GetInt(DataReader.LAYOUT_STATE, context)
        );
        switch (layoutState) {
            case _123:
            default:
                state = "1 2 3";
                break;
            case _789:
                state = "7 8 9";
                break;
        }
        numberstv.setText(state);
        numberstv.setOnClickListener(this::layoutDropdown);


        TextView buttonstv = view.findViewById(R.id.buttonsPlace);
        ButtonsPlace buttonsPlace = ButtonsPlace.convert(
                DataReader.GetInt(DataReader.BUTTONS_PLACE, context)
        );

        switch (buttonsPlace) {
            case RIGHT:
            default:
                state = "Справа";
                break;
            case LEFT:
                state = "Слева";
                break;
        }
        buttonstv.setText(state);
        buttonstv.setOnClickListener(this::buttonsDropdown);

        view = v;
        IndicatorSeekBar seekBar = view.findViewById(R.id.round_length_slider);
        seekBar.customTickTexts(new String[]{"1", "2", "3", "5", "10", "15", "20", "30", "45", "     60"});
        switch (DataReader.GetInt(DataReader.ROUND_TIME, getContext())) {
            case 1:
                seekBar.setProgress(0);
                break;
            case 2:
                seekBar.setProgress(11);
                break;
            case 3:
                seekBar.setProgress(22);
                break;
            case 5:
                seekBar.setProgress(33);
                break;
            case 10:
                seekBar.setProgress(44);
                break;
            case 15:
                seekBar.setProgress(55);
                break;
            case 20:
                seekBar.setProgress(66);
                break;
            case 30:
                seekBar.setProgress(77);
                break;
            case 45:
                seekBar.setProgress(88);
                break;
            case 60:
                seekBar.setProgress(100);
                break;
        }
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                String value = seekParams.tickText.trim();
                DataReader.SaveInt(Integer.parseInt(value), DataReader.ROUND_TIME, getContext());
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
        switch (DataReader.GetInt("DisapRoundTime", getContext())) {
            case 1:
                seekBar.setProgress(0);
                break;
            case 2:
                seekBar.setProgress(11);
                break;
            case 3:
                seekBar.setProgress(22);
                break;
            case 5:
                seekBar.setProgress(33);
                break;
            case 10:
                seekBar.setProgress(44);
                break;
            case 15:
                seekBar.setProgress(55);
                break;
            case 20:
                seekBar.setProgress(66);
                break;
            case 30:
                seekBar.setProgress(77);
                break;
            case 45:
                seekBar.setProgress(88);
                break;
            case -1:
                seekBar.setProgress(100);
                break;
        }
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
//                Log.i(TAG, seekParams.seekBar);
//                Log.i(TAG, seekParams.progress);
//                Log.i(TAG, seekParams.progressFloat);
//                Log.i(TAG, seekParams.fromUser);
//                //when tick count > 0
//                Log.i(TAG, seekParams.thumbPosition);
//                Log.i(TAG, seekParams.tickText);
                String value = seekParams.tickText.trim();
                int ivalue = 0;
                if (value.equals(NODISAPEARCHAR))
                    ivalue = -1;
                else
                    ivalue = Integer.parseInt(value);
                DataReader.SaveInt(ivalue, DataReader.DISAP_ROUND_TIME, getContext());
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            }
        });

        TextView goal = view.findViewById(R.id.goal);
        state = DataReader.GetInt(DataReader.GOAL, context) + " мин";
        goal.setText(state);
        goal.setOnClickListener(this::goalDropdown);

//        TextView themetv = view.findViewById(R.id.theme);
//        Theme theme = Theme.convert(
//                DataReader.GetValue("Theme", context)
//        );
//        switch (theme) {
//            case LIGHT:
//                state = "Светлая";
//                break;
//            case DARK:
//            default:
//                state = "Темная";
//                break;
//            case SYSTEM:
//                state = "Системная";
//                break;
//        }
//        themetv.setText(state);
//        themetv.setOnClickListener(this::themeDropdown);

        TextView id = view.findViewById(R.id.id);
        id.setText(FireBaseUtils.getUser().getUid());

//        TextView emailtv = view.findViewById(R.id.email);
//        String email = user.getEmail();
//        if (email.isEmpty())
//            emailtv.setText("Не зарегистрирован");
//        else
//            emailtv.setText(user.getEmail());

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

        if (BuildConfig.FLAVOR.equals("decimals") &&
                BuildConfig.BUILD_TYPE.equals("debug") &&
                BuildConfig.VERSION_NAME == "0.9.0") {
            TextView account = view.findViewById(R.id.account_text);
            account.setVisibility(View.GONE);
            view.findViewById(R.id.id_layout).setVisibility(View.GONE);
            view.findViewById(R.id.queue_layout).setVisibility(View.GONE);
//            view.findViewById(R.id.goal_layout).setVisibility(View.GONE);
            view.findViewById(R.id.reminder_layout).setVisibility(View.GONE);
            view.findViewById(R.id.remind_time_layout).setVisibility(View.GONE);
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            view.findViewById(R.id.reminder_layout).setVisibility(View.GONE);
            view.findViewById(R.id.remind_time_layout).setVisibility(View.GONE);
        } else
            reminder_time.setOnClickListener(this::reminderDropDown);
    }

    private void toggleQueue(View v) {
        boolean on = ((SwitchCompat) view.findViewById(R.id.queue)).isChecked();
        DataReader.SaveBoolean(on, DataReader.QUEUE, context);
    }

    public void timerDropdown(View v) {
        final TextView timerStateText = view.findViewById(R.id.TimerState);
        final PopupMenu popup = new PopupMenu(context, timerStateText);

        popup.getMenuInflater().inflate(R.menu.settings_timer, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int state = 0;
            timerStateText.setText(item.getTitle().toString());
            switch (item.getTitle().toString()) {
                case "Непрерывный":
                    state = TimerState.convert(TimerState.CONTINIOUS);
                    break;
                case "По шагам":
                    state = TimerState.convert(TimerState.DISCRETE);
                    break;
                case "Нет":
                    state = TimerState.convert(TimerState.INVISIBlE);
                    break;
            }
            DataReader.SaveInt(state, DataReader.TIMER_STATE, context);
            return true;
        });

        popup.show();
    }

    public void layoutDropdown(View v) {
        final TextView numbersLayout = view.findViewById(R.id.numbersLayout);
        final PopupMenu popup = new PopupMenu(context, numbersLayout);

        popup.getMenuInflater().inflate(R.menu.settings_layout, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int state = 0;
            numbersLayout.setText(item.getTitle().toString());
            switch (item.getTitle().toString()) {
                case "1 2 3":
                default:
                    state = LayoutState.convert(LayoutState._123);
                    break;
                case "7 8 9":
                    state = LayoutState.convert(LayoutState._789);
                    break;
            }
            DataReader.SaveInt(state, DataReader.LAYOUT_STATE, context);
            return true;
        });
        popup.show();
    }

    public void buttonsDropdown(View v) {
        final TextView buttonsplace = view.findViewById(R.id.buttonsPlace);
        final PopupMenu popup = new PopupMenu(context, buttonsplace);

        popup.getMenuInflater().inflate(R.menu.settings_buttonsplace, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int state = 0;
            buttonsplace.setText(item.getTitle().toString());
            switch (item.getTitle().toString()) {
                case "Справа":
                default:
                    state = ButtonsPlace.convert(ButtonsPlace.RIGHT);
                    break;
                case "Слева":
                    state = ButtonsPlace.convert(ButtonsPlace.LEFT);
                    break;
            }
            DataReader.SaveInt(state, DataReader.BUTTONS_PLACE, context);
            return true;
        });
        popup.show();
    }

    public void goalDropdown(View v) {
        final TextView goal = view.findViewById(R.id.goal);
        final PopupMenu popup = new PopupMenu(context, goal);

        popup.getMenuInflater().inflate(R.menu.settings_goal, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int minutes = 0;
            goal.setText(item.getTitle().toString());
            switch (item.getTitle().toString()) {
                case "5 мин":
                default:
                    minutes = 5;
                    break;
                case "10 мин":
                    minutes = 10;
                    break;
                case "15 мин":
                    minutes = 15;
                    break;
            }
            DataReader.SaveInt(minutes, DataReader.GOAL, context);
            return true;
        });
        popup.show();
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