package com.education4all.decimals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.education4all.decimals.MathCoachAlg.DataReader;

public class SettingsInterfaceTab extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_interface, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        view = v;
        TextView timerstate = view.findViewById(R.id.TimerState);
        String state = "";
        switch (DataReader.GetTimerState(getContext())) {
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
        switch (DataReader.GetLayoutState(getContext())) {
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
        switch (DataReader.GetButtonsPlace(getContext())) {
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
    }

    public void timerDropdown(View view) {
        final TextView timerStateText = view.findViewById(R.id.TimerState);
        final PopupMenu popup = new PopupMenu(getContext(), timerStateText);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.timerdropdown, popup.getMenu());

        //registering popup with OnMenuItemClickListener
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
            DataReader.SaveTimerState(state, getActivity().getApplicationContext());
            return true;
        });

        popup.show(); //showing popup menu
    }

    public void layoutDropdown(View view) {
        final TextView numbersLayout = view.findViewById(R.id.numbersLayout);
        final PopupMenu popup = new PopupMenu(getContext(), numbersLayout);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.layoutdropdown, popup.getMenu());

        //registering popup with OnMenuItemClickListener
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
            DataReader.SaveLayoutState(state, getActivity().getApplicationContext());
            return true;
        });
        popup.show(); //showing popup menu
    }

    public void buttonsDropdown(View view) {
        final TextView buttonsplace = view.findViewById(R.id.buttonsPlace);
        final PopupMenu popup = new PopupMenu(getContext(), buttonsplace);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.buttonsplacedropdown, popup.getMenu());

        //registering popup with OnMenuItemClickListener
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
            DataReader.SaveButtonsPlace(state, getActivity().getApplicationContext());
            return true;
        });
        popup.show(); //showing popup menu
    }
}
