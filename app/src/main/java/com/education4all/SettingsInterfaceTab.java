package com.education4all;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.education4all.MathCoachAlg.DataReader;

public class SettingsInterfaceTab extends Fragment {
    View view;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_interface, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        context = getContext();
        view = v;
        TextView timerstate = view.findViewById(R.id.TimerState);
        String state = "";
        switch (DataReader.GetValue("TimerState", context)) {
            case 0:
                state = "Непрерывный";
                break;
            case 1:
                state = "По шагам";
                break;
            case 2:
                state = "Нет";
                break;
        }
        timerstate.setText(state);
        timerstate.setOnClickListener(this::timerDropdown);

        TextView numberslayout = view.findViewById(R.id.numbersLayout);
        state = "";
        switch (DataReader.GetValue("LayoutState", context)) {
            case 0:
            default:
                state = "1 2 3";
                break;
            case 1:
                state = "7 8 9";
                break;
        }
        numberslayout.setText(state);
        numberslayout.setOnClickListener(this::layoutDropdown);


        TextView buttonsplace = view.findViewById(R.id.buttonsPlace);
        state = "";
        switch (DataReader.GetValue("ButtonsPlace", context)) {
            case 0:
            default:
                state = "Справа";
                break;
            case 1:
                state = "Слева";
                break;
        }
        buttonsplace.setText(state);
        buttonsplace.setOnClickListener(this::buttonsDropdown);

        TextView goal = view.findViewById(R.id.goal);
        state = "";
        state = DataReader.GetValue("Goal", context) + " мин";
        goal.setText(state);
        goal.setOnClickListener(this::goalDropdown);

        TextView theme = view.findViewById(R.id.theme);
        state = "";
        switch (DataReader.GetValue("Theme", context)) {
            case 1:
                state = "Светлая";
                break;
            case -1:
            default:
                state = "Темная";
                break;
            case 0:
                state = "Системная";
                break;
        }
        theme.setText(state);
        theme.setOnClickListener(this::themeDropdown);
    }

    public void timerDropdown(View view) {
        final TextView timerStateText = view.findViewById(R.id.TimerState);
        final PopupMenu popup = new PopupMenu(context, timerStateText);

        popup.getMenuInflater().inflate(R.menu.settings_timer, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int state = 0;
            timerStateText.setText(item.getTitle().toString());
            switch (item.getTitle().toString()) {
                case "Непрерывный":
                    state = 0;
                    break;
                case "По шагам":
                    state = 1;
                    break;
                case "Нет":
                    state = 2;
                    break;
            }
            DataReader.SaveValue(state, "TimerState", context);
            return true;
        });

        popup.show();
    }

    public void layoutDropdown(View view) {
        final TextView numbersLayout = view.findViewById(R.id.numbersLayout);
        final PopupMenu popup = new PopupMenu(context, numbersLayout);

        popup.getMenuInflater().inflate(R.menu.settings_layout, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int state = 0;
            numbersLayout.setText(item.getTitle().toString());
            switch (item.getTitle().toString()) {
                case "1 2 3":
                default:
                    state = 0;
                    break;
                case "7 8 9":
                    state = 1;
                    break;
            }
            DataReader.SaveValue(state, "LayoutState", context);
            return true;
        });
        popup.show();
    }

    public void buttonsDropdown(View view) {
        final TextView buttonsplace = view.findViewById(R.id.buttonsPlace);
        final PopupMenu popup = new PopupMenu(context, buttonsplace);

        popup.getMenuInflater().inflate(R.menu.settings_buttonsplace, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int state = 0;
            buttonsplace.setText(item.getTitle().toString());
            switch (item.getTitle().toString()) {
                case "Справа":
                default:
                    state = 0;
                    break;
                case "Слева":
                    state = 1;
                    break;
            }
            DataReader.SaveValue(state, "ButtonsPlace", context);
            return true;
        });
        popup.show();
    }

    public void goalDropdown(View view) {
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
            DataReader.SaveValue(minutes, "Goal", context);
            return true;
        });
        popup.show();
    }

    public void themeDropdown(View view) {
        final TextView theme = view.findViewById(R.id.theme);
        final PopupMenu popup = new PopupMenu(context, theme);

        popup.getMenuInflater().inflate(R.menu.settings_theme, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int state = 0;
            theme.setText(item.getTitle().toString());
            //светлая и темная тема меняются местами, чтобы в IDE отображалась темная
            switch (item.getTitle().toString()) {
                case "Светлая":
                default:
                    state = 1;
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case "Темная":
                    state = -1;
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case "Системная":
                    state = 0;
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
            }
            DataReader.SaveValue(state, "Theme", context);
            return true;
        });
        popup.show();
    }
}
