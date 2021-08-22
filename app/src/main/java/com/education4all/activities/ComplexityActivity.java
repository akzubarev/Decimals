package com.education4all.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.education4all.BuildConfig;
import com.education4all.R;
import com.education4all.mathCoachAlg.DataReader;


public class ComplexityActivity extends AppCompatActivity {
    private int actionType;
    public static final String COMPLEXITY_SETTINGS = "ComplexitySettings";
    public static final String ADD = "\u2006+\u2006";
    public static final String SUB = "\u2006−\u2006";
    public static final String MUL = "\u2006\u22C5\u2006";
    public static final String DIV = "\u2006:\u2006";
    final String id = BuildConfig.APPLICATION_ID;
    boolean isIntegers = BuildConfig.FLAVOR.equals("integers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complexity);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        actionType = extras.getInt("Type");

        boolean checked = false;
        for (int i = 1; i < 5; i++) {
            int resID = getResources().getIdentifier("checkBox" + i, "id", id);
            CheckBox chk = findViewById(resID);
            checked = DataReader.checkComplexity(actionType - 1, i - 1, this);
            if (chk.isChecked() != checked)
                chk.setChecked(checked);
        }

        if (isIntegers && actionType == 4) {
            CheckBox chk = findViewById(R.id.checkBox5);
            checked = DataReader.checkComplexity(actionType - 1, 4, this);
            if (chk.isChecked() != checked)
                chk.setChecked(checked);
            LinearLayout layout5 = findViewById(R.id.layout5);
            LinearLayout biglayout = (LinearLayout) layout5.getParent();
            biglayout.removeView(layout5);
            biglayout.addView(layout5, 3);

        } else {
            LinearLayout layout = findViewById(R.id.layout5);
            ((ViewManager) layout.getParent()).removeView(layout);
//            ((ViewManager) chk.getParent()).removeView(chk);
//            TextView tw1 = findViewById(R.id.textView51);
//            ((ViewManager) tw1.getParent()).removeView(tw1);
//            TextView tw2 = findViewById(R.id.textView52);
//            ((ViewManager) tw2.getParent()).removeView(tw2);
        }

        // StupidTextFill(actionType);
        CleverTextFill();
    }


    private void CleverTextFill() {
        TextView tvname, tvexample;
        int tvnameID, tvexampleID;
        int nameID, exampleID, limit = 5;
        String operation, symbol, name, example;

        TextView complType = findViewById(R.id.complTextView);
        switch (actionType) {
            case 1:
            default:
                operation = "addition";
                symbol = ADD;
                complType.setText("Сложение");
                break;
            case 2:
                operation = "substraction";
                symbol = SUB;
                complType.setText("Вычитание");
                break;
            case 3:
                operation = "multiplication";
                symbol = MUL;
                complType.setText("Умножение");
                break;
            case 4:
                operation = "division";
                symbol = DIV;
                complType.setText("Деление");
                if (isIntegers)
                    limit = 6;
                break;
        }

        for (int i = 1; i < limit; i++) {
            tvnameID = getResources().getIdentifier("textView" + i + 1, "id", id);
            tvexampleID = getResources().getIdentifier("textView" + i + 2, "id", id);
            tvname = findViewById(tvnameID);
            tvexample = findViewById(tvexampleID);

            nameID = getResources().getIdentifier(operation + "Name" + i, "string", id);
            exampleID = getResources().getIdentifier(operation + "Example" + i, "string", id);
            name = getString(nameID);
            example = getString(exampleID);

            tvname.setText(name);
            tvexample.setText(String.format(example, symbol, symbol, symbol));
        }
    }

    @Override
    public void onBackPressed() {
        String action = "";
        StringBuilder currentComplexity = new StringBuilder();
        SharedPreferences.Editor editor = getSharedPreferences(COMPLEXITY_SETTINGS, MODE_PRIVATE).edit();
        switch (actionType) {
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
        CheckBox chk = findViewById(R.id.checkBox1);
        if (chk.isChecked()) {
            currentComplexity.append("0").append(",");
        }
        chk = findViewById(R.id.checkBox2);
        if (chk.isChecked()) {
            currentComplexity.append("1").append(",");
        }
        chk = findViewById(R.id.checkBox3);
        if (chk.isChecked()) {
            currentComplexity.append("2").append(",");
        }
        chk = findViewById(R.id.checkBox4);
        if (chk.isChecked()) {
            currentComplexity.append("3").append(",");
        }
        if (isIntegers && actionType == 4) {
            chk = findViewById(R.id.checkBox5);
            if (chk.isChecked()) {
                currentComplexity.append("4").append(",");
            }
        }

        editor.putString(action, currentComplexity.toString());
        editor.apply();
        finish();
    }

    @Override
    protected void onDestroy() {
        onBackPressed();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        onBackPressed();
        super.onPause();
    }
}