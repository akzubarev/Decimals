package com.education4all.activities.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.education4all.BuildConfig;
import com.education4all.R;
import com.education4all.mathCoachAlg.DataReader;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class SettingsTaskTab extends Fragment {

    public static final String COMPLEXITY_SETTINGS = "ComplexitySettings";
    private View view;
    private final String id = BuildConfig.APPLICATION_ID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_tasks, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        view = v;
        if (BuildConfig.FLAVOR.equals("integers")) {
            final CheckBox newDivisionCHeckbox = v.findViewById(R.id.checkBoxDiv2);
            newDivisionCHeckbox.setOnClickListener(v1 -> {
                        newDivisionCHeckbox.setChecked(!newDivisionCHeckbox.isChecked());
                        Context wrapper = new ContextThemeWrapper(getContext(), R.style.CustomPopup);
                        final PopupMenu popup = new PopupMenu(wrapper, newDivisionCHeckbox);
                        popup.getMenuInflater()
                                .inflate(R.menu.dropdown_div, popup.getMenu());

                        popup.setOnMenuItemClickListener(item -> {
                            item.setChecked(!item.isChecked());
                            boolean one = false;
                            boolean two = false;
                            if (popup.getMenu().getItem(0).isChecked()) {
                                one = true;
                            }
                            if (popup.getMenu().getItem(1).isChecked()) {
                                two = true;
                            }
                            addComplexityFromPopup(one, two);
                            newDivisionCHeckbox.setChecked(one || two);
                            return true;
                        });

                        popup.show();
                        if (DataReader.checkComplexity(3, 1, getContext())) {
                            popup.getMenu().getItem(0).setChecked(true);
                        }
                        if (DataReader.checkComplexity(3, 4, getContext())) {
                            popup.getMenu().getItem(1).setChecked(true);
                        }
                    }
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String[] names = new String[]{"checkBoxAdd", "checkBoxSub", "checkBoxMul", "checkBoxDiv"};
        for (int operation = 0; operation < 4; operation++)
            for (int i = 1; i <= 4; ++i) {
                String name = names[operation] + i;
                boolean checked = false;
                int resID = getResources().getIdentifier(name, "id", id);
                CheckBox cb = view.findViewById(resID);

                if (BuildConfig.FLAVOR.equals("integers") && operation == 3 && i == 2) {
                    checked = DataReader.checkComplexity(3, 1, getActivity().getApplicationContext())
                            || DataReader.checkComplexity(3, 4, getActivity().getApplicationContext());
                } else {
                    checked = DataReader.checkComplexity(operation, i - 1, getActivity().getApplicationContext());
                    cb.setOnClickListener(this::onCheckBoxClick);
                }
                if (cb.isChecked() != checked)
                    cb.setChecked(checked);
            }
    }

    public void onCheckBoxClick(View v) {
        StringBuilder currentComplexity = new StringBuilder();
        SharedPreferences.Editor editor = getActivity().getApplication()
                .getSharedPreferences(COMPLEXITY_SETTINGS, AppCompatActivity.MODE_PRIVATE).edit();
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


        for (int i = 1; i < 5; i++) {
            String name = "checkBox" + action + i;
            int resID = getResources().getIdentifier(name, "id", id);
            CheckBox chk = view.findViewById(resID);

            if (chk.isChecked()) {
                currentComplexity.append(i - 1).append(",");
            }
        }

        editor.putString(action, currentComplexity.toString());
        editor.apply();
    }

    void addComplexityFromPopup(boolean one, boolean two) {
        StringBuilder currentComplexity = new StringBuilder();
        SharedPreferences.Editor editor = getContext().getSharedPreferences(COMPLEXITY_SETTINGS, Context.MODE_PRIVATE).edit();

        CheckBox chk = view.findViewById(R.id.checkBoxDiv1);
        if (chk.isChecked()) {
            currentComplexity.append("0").append(",");
        }
        if (one) {
            currentComplexity.append("1").append(",");
        }

        chk = view.findViewById(R.id.checkBoxDiv3);
        if (chk.isChecked()) {
            currentComplexity.append("2").append(",");
        }

        chk = view.findViewById(R.id.checkBoxDiv4);
        if (chk.isChecked()) {
            currentComplexity.append("3").append(",");
        }
        if (two) {
            currentComplexity.append("4").append(",");
        }

        editor.putString("Div", currentComplexity.toString());
        editor.apply();
    }


}
