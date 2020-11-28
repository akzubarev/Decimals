package com.education4all.decimals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.education4all.decimals.MathCoachAlg.DataReader;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class SettingsSimpleActivity extends AppCompatActivity {

    public static final String COMPLEXITY_SETTINGS = "ComplexitySettings";
    public static final String NODISAPEARCHAR = "∞"; //DecimalFormatSymbols.getInstance().getInfinity()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_simple);

        ActionBar myActionBar = getSupportActionBar();
        myActionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        TextView timerstate = findViewById(R.id.TimerState);
        String state = "";
        switch (DataReader.GetTimerState(this)) {
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

//        Button button = (Button) findViewById(R.id.firstMinus);
//        final View v = new View(this);
//        button.setOnTouchListener(new RepeatListener(400, 200, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                firstMinus(v);
//            }
//        }));
//        button = (Button) findViewById(R.id.secondMinus);
//        button.setOnTouchListener(new RepeatListener(400, 200, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                secondMinus(v);
//            }
//        }));
//        button = (Button) findViewById(R.id.firstPlus);
//        button.setOnTouchListener(new RepeatListener(400, 200, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                firstPlus(v);
//            }
//        }));
//        button = (Button) findViewById(R.id.secondPlus);
//        button.setOnTouchListener(new RepeatListener(400, 200, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                secondPlus(v);
//            }
//        }));

//        final CheckBox newDivisionCHeckbox = (CheckBox) findViewById(R.id.checkBoxDiv2);
//        newDivisionCHeckbox.setOnClickListener(new View.OnClickListener() {
//                                                   @Override
//                                                   public void onClick(View v) {
//                                                       newDivisionCHeckbox.setChecked(!newDivisionCHeckbox.isChecked());
//                                                       //Creating the instance of PopupMenu
//                                                       final PopupMenu popup = new PopupMenu(SettingsSimpleActivity.this, newDivisionCHeckbox);
//                                                       //Inflating the Popup using xml file
//                                                       popup.getMenuInflater()
//                                                               .inflate(R.menu.divdropdown, popup.getMenu());
//
//                                                       //registering popup with OnMenuItemClickListener
//                                                       popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                                                           public boolean onMenuItemClick(MenuItem item) {
//                                                               item.setChecked(!item.isChecked());
//                                                               boolean one = false;
//                                                               boolean two = false;
//                                                               if (popup.getMenu().getItem(0).isChecked()) {
//                                                                   one = true;
//                                                               }
//                                                               if (popup.getMenu().getItem(1).isChecked()) {
//                                                                   two = true;
//                                                               }
//                                                               addComplexityFromPopup(one, two);
//                                                               if (one || two) {
//                                                                   newDivisionCHeckbox.setChecked(true);
//                                                               } else {
//                                                                   newDivisionCHeckbox.setChecked(false);
//                                                               }
//                                                               return true;
//                                                           }
//                                                       });
//
//                                                       popup.show(); //showing popup menu
//                                                       MenuItem item1 = (MenuItem) findViewById(R.id.one);
//                                                       if (DataReader.checkComplexity(3, 1, SettingsSimpleActivity.this)) {
//                                                           popup.getMenu().getItem(0).setChecked(true);
//                                                       }
//                                                       if (DataReader.checkComplexity(3, 4, SettingsSimpleActivity.this)) {
//                                                           popup.getMenu().getItem(1).setChecked(true);
//                                                       }
//                                                   }
//                                               }
//
//        ); //closing the setOnClickListener method

        IndicatorSeekBar seekBar = findViewById(R.id.round_length_slider);
        seekBar.customTickTexts(new String[]{"1", "2", "3", "5", "10", "15", "20", "30", "45", "     60"});
        switch ((int) DataReader.GetRoundTime(getApplicationContext())) {
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
                DataReader.SaveRoundTime(fvalue, getApplicationContext());
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            }
        });


        seekBar = findViewById(R.id.disappear_time_slider);
        seekBar.customTickTexts(new String[]{"1", "2", "3", "5", "10", "15", "20", "30", "45", "    ∞"});
        switch ((int) DataReader.GetDisapRoundTime(getApplicationContext())) {
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
                DataReader.SaveDisapRoundTime(fvalue, getApplicationContext());
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            }
        });
    }

    void addComplexityFromPopup(boolean one, boolean two) {
        StringBuilder currentComplexity = new StringBuilder();
        SharedPreferences.Editor editor = getSharedPreferences(COMPLEXITY_SETTINGS, MODE_PRIVATE).edit();

        CheckBox chk = (CheckBox) findViewById(R.id.checkBoxDiv1);
        if (chk.isChecked()) {
            currentComplexity.append("0").append(",");
        }
        if (one) {
            currentComplexity.append("1").append(",");
        }

        chk = (CheckBox) findViewById(R.id.checkBoxDiv3);
        if (chk.isChecked()) {
            currentComplexity.append("2").append(",");
        }

        chk = (CheckBox) findViewById(R.id.checkBoxDiv4);
        if (chk.isChecked()) {
            currentComplexity.append("3").append(",");
        }
        if (two) {
            currentComplexity.append("4").append(",");
        }

        editor.putString("Div", currentComplexity.toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 1; i <= 4; ++i) {
            String name = "checkBoxAdd" + Integer.toString(i);
            int resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
            CheckBox cb = (CheckBox) findViewById(resID);
            if (DataReader.checkComplexity(0, i - 1, this)) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }
        }
        for (int i = 1; i <= 4; ++i) {
            String name = "checkBoxSub" + Integer.toString(i);
            int resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
            CheckBox cb = (CheckBox) findViewById(resID);
            if (DataReader.checkComplexity(1, i - 1, this)) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }
        }
        for (int i = 1; i <= 4; ++i) {
            String name = "checkBoxMul" + Integer.toString(i);
            int resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
            CheckBox cb = (CheckBox) findViewById(resID);
            if (DataReader.checkComplexity(2, i - 1, this)) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }
        }
        for (int i = 1; i <= 4; ++i) {
           // if (i != 2) {
                String name = "checkBoxDiv" + Integer.toString(i);
                int resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
                CheckBox cb = (CheckBox) findViewById(resID);
                if (DataReader.checkComplexity(3, i - 1, this)) {
                    cb.setChecked(true);
                } else {
                    cb.setChecked(false);
                }
//            } else {
//                String name = "checkBoxDiv" + Integer.toString(i);
//                int resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
//                CheckBox cb = (CheckBox) findViewById(resID);
//                if (DataReader.checkComplexity(3, 1, this) || DataReader.checkComplexity(3, 4, this)) {
//                    cb.setChecked(true);
//                } else {
//                    cb.setChecked(false);
//                }
//            }
        }
//        CheckBox cb = (CheckBox) findViewById(R.id.checkBoxAdd1);
//        DisplayMetrics metrics = new DisplayMetrics();
//        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
//        wm.getDefaultDisplay().getMetrics(metrics);
//        final float width = metrics.widthPixels / 2 - 20;
//        final float height = metrics.heightPixels / 8;
////        LinearLayout.LayoutParams layoutParamsWidth50 = new LinearLayout.LayoutParams(
////                (int) (width) , (int) (height) );
////        Button currentButton = (Button)findViewById(R.id.buttonAdd);
////        currentButton.setLayoutParams(layoutParamsWidth50);
////        currentButton = (Button)findViewById(R.id.buttonSub);
////        currentButton.setLayoutParams(layoutParamsWidth50);
////        currentButton = (Button)findViewById(R.id.buttonMul);
////        currentButton.setLayoutParams(layoutParamsWidth50);
////        currentButton = (Button)findViewById(R.id.buttonDiv);
////        currentButton.setLayoutParams(layoutParamsWidth50);
    }

    public void startCompl(View view) {
        Intent intent = new Intent(this, ComplexityActivity.class);
        int p = Integer.parseInt((String) view.getTag());
        intent.putExtra("Type", p);
        startActivity(intent);
    }

    public boolean textIsInt(String text) {
        if (text.length() == 0) {
            return false;
        }

        boolean isInt;
        for (int i = 0; i < text.length(); ++i) {
            isInt = false;
            for (int j = 0; j <= 9; ++j) {
                String a = text.substring(i, i + 1);
                String n = Long.toString((long) j);
                if (a.equals(n)) {
                    isInt = true;
                }
            }
            if (!isInt) {
                return false;
            }
        }
        return true;
    }

    public void onCheckBoxClick(View view) {
        StringBuilder currentComplexity = new StringBuilder();
        SharedPreferences.Editor editor = getSharedPreferences(COMPLEXITY_SETTINGS, MODE_PRIVATE).edit();
        String action = "";
        int tag = Integer.parseInt(view.getTag().toString());
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
        CheckBox chk = (CheckBox) findViewById(resID);
        if (chk.isChecked()) {
            currentComplexity.append("0").append(",");
        }

        name = "checkBox" + action + "2";
        resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
        chk = (CheckBox) findViewById(resID);
        if (chk.isChecked()) {
            currentComplexity.append("1").append(",");
        }

        name = "checkBox" + action + "3";
        resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
        chk = (CheckBox) findViewById(resID);
        if (chk.isChecked()) {
            currentComplexity.append("2").append(",");
        }

        name = "checkBox" + action + "4";
        resID = getResources().getIdentifier(name, "id", "com.education4all.decimals");
        chk = (CheckBox) findViewById(resID);
        if (chk.isChecked()) {
            currentComplexity.append("3").append(",");
        }
//        if (action.equals("Div")) {
//            name = "checkBox" + action + "5";
//            resID = getResources().getIdentifier(name, "id", "com.education4all.mathcoach");
//            chk = (CheckBox)findViewById(resID);
//            if (chk.isChecked()) {
//                currentComplexity.append("4").append(",");
//            }
//        }

        editor.putString(action, currentComplexity.toString());
        editor.apply();
    }

    public void timerDropdown(View view) {
        final TextView timerStateText = findViewById(R.id.TimerState);
        final PopupMenu popup = new PopupMenu(SettingsSimpleActivity.this, timerStateText);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.timerdropdown, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
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
                DataReader.SaveTimerState(state, getApplicationContext());
                return true;
            }
        });

        popup.show(); //showing popup menu
        MenuItem item1 = (MenuItem) findViewById(R.id.one);
        if (DataReader.checkComplexity(3, 1, SettingsSimpleActivity.this)) {
            popup.getMenu().getItem(0).setChecked(true);
        }
        if (DataReader.checkComplexity(3, 4, SettingsSimpleActivity.this)) {
            popup.getMenu().getItem(1).setChecked(true);
        }

    }
}
