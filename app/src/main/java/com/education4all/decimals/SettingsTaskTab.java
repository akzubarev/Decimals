package com.education4all.decimals;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.education4all.decimals.MathCoachAlg.DataReader;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class SettingsTaskTab extends Fragment {

    public static final String COMPLEXITY_SETTINGS = "ComplexitySettings";
    private View view;
    public static final String NODISAPEARCHAR = "∞"; //DecimalFormatSymbols.getInstance().getInfinity()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_tasks, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        view = v;
        IndicatorSeekBar seekBar = view.findViewById(R.id.round_length_slider);
        seekBar.customTickTexts(new String[]{"1", "2", "3", "5", "10", "15", "20", "30", "45", "     60"});
        switch ((int) DataReader.GetRoundTime(getActivity().getApplicationContext())) {
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
//                Log.i(TAG, seekParams.seekBar);
//                Log.i(TAG, seekParams.progress);
//                Log.i(TAG, seekParams.progressFloat);
//                Log.i(TAG, seekParams.fromUser);
//                //when tick count > 0
//                Log.i(TAG, seekParams.thumbPosition);
//                Log.i(TAG, seekParams.tickText);
                String value = seekParams.tickText;
                float fvalue = 0;
                if (value.equals(NODISAPEARCHAR))
                    fvalue = -1;
                else
                    fvalue = Float.parseFloat(value);
                DataReader.SaveRoundTime(fvalue, getActivity().getApplicationContext());
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
        switch ((int) DataReader.GetDisapRoundTime(getActivity().getApplicationContext())) {
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
                float fvalue = 0;
                if (value.equals(NODISAPEARCHAR))
                    fvalue = -1;
                else
                    fvalue = Float.parseFloat(value);
                DataReader.SaveDisapRoundTime(fvalue, getActivity().getApplicationContext());
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        boolean checked = false;
        for (int i = 1; i <= 4; ++i) {
            String name = "checkBoxAdd" + i;
            int resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
            CheckBox cb = (CheckBox) view.findViewById(resID);
            checked = DataReader.checkComplexity(0, i - 1, getActivity().getApplicationContext());
            if (cb.isChecked() != checked)
                cb.setChecked(checked);
            cb.setOnClickListener(this::onCheckBoxClick);
        }
        for (int i = 1; i <= 4; ++i) {
            String name = "checkBoxSub" + i;
            int resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
            CheckBox cb = (CheckBox) view.findViewById(resID);
            checked = DataReader.checkComplexity(1, i - 1, getActivity().getApplicationContext());
            if (cb.isChecked() != checked)
                cb.setChecked(checked);
            cb.setOnClickListener(this::onCheckBoxClick);
        }
        for (int i = 1; i <= 4; ++i) {
            String name = "checkBoxMul" + i;
            int resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
            CheckBox cb = (CheckBox) view.findViewById(resID);
            checked = DataReader.checkComplexity(2, i - 1, getActivity().getApplicationContext());
            if (cb.isChecked() != checked)
                cb.setChecked(checked);
            cb.setOnClickListener(this::onCheckBoxClick);
        }
        for (int i = 1; i <= 4; ++i) {
            //if (i != 2) {
            String name = "checkBoxDiv" + i;
            int resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
            CheckBox cb = (CheckBox) view.findViewById(resID);
            checked = DataReader.checkComplexity(3, i - 1, getActivity().getApplicationContext());
            if (cb.isChecked() != checked)
                cb.setChecked(checked);
            cb.setOnClickListener(this::onCheckBoxClick);
        }
    }

    public void onCheckBoxClick(View v) {
        StringBuilder currentComplexity = new StringBuilder();
        SharedPreferences.Editor editor = getActivity().getApplication().getSharedPreferences(COMPLEXITY_SETTINGS, AppCompatActivity.MODE_PRIVATE).edit();
        String action = "";
        int tag = Integer.parseInt(v.getTag().toString());
        switch (tag) {
            case 1:
                action = "Add";
                break;
            case 2:
                action = "Sub";
                break;
            case 3:
                action = "Mul";
                break;
            case 4:
                action = "Div";
                break;
        }
        String name = "checkBox" + action + "1";
        int resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
        CheckBox chk = (CheckBox) view.findViewById(resID);

        if (chk.isChecked()) {
            currentComplexity.append("0").append(",");
        }

        name = "checkBox" + action + "2";
        resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
        chk = (CheckBox) view.findViewById(resID);
        if (chk.isChecked()) {
            currentComplexity.append("1").append(",");
        }

        name = "checkBox" + action + "3";
        resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
        chk = (CheckBox) view.findViewById(resID);
        if (chk.isChecked()) {
            currentComplexity.append("2").append(",");
        }

        name = "checkBox" + action + "4";
        resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
        chk = (CheckBox) view.findViewById(resID);
        if (chk.isChecked()) {
            currentComplexity.append("3").append(",");
        }

        editor.putString(action, currentComplexity.toString());
        editor.apply();
    }

}
