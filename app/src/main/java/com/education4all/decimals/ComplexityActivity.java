package com.education4all.decimals;

import android.content.SharedPreferences;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.ViewManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education4all.decimals.MathCoachAlg.DataReader;


public class ComplexityActivity extends AppCompatActivity {
    private int actionType;
    public static final String COMPLEXITY_SETTINGS = "ComplexitySettings";
    public static final String ADD = "\u2006+\u2006";
    public static final String SUB = "\u2006−\u2006";
    public static final String MUL = "\u2006\u22C5\u2006";
    public static final String DIV = "\u2006:\u2006";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complexity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        actionType = extras.getInt("Type");
//        if (actionType != 4) {
//            CheckBox chk1 = (CheckBox) findViewById(R.id.checkBox5);
//            ((ViewManager) chk1.getParent()).removeView(chk1);
//            TextView tw1 = (TextView) findViewById(R.id.textView7);
//            ((ViewManager) tw1.getParent()).removeView(tw1);
//        }
        boolean checked = false;
        CheckBox chk = (CheckBox) findViewById(R.id.checkBox1);
        checked = DataReader.checkComplexity(actionType - 1, 0, this);
        if (chk.isChecked() != checked)
            chk.setChecked(checked);

        chk = (CheckBox) findViewById(R.id.checkBox2);
        checked = DataReader.checkComplexity(actionType - 1, 1, this);
        if (chk.isChecked() != checked)
            chk.setChecked(checked);
//        chk = (CheckBox) findViewById(R.id.checkBox5);
//        if (actionType == 4 && DataReader.checkComplexity(actionType - 1, 4, this)) {
//            check_old = true;
//        }
        chk = (CheckBox) findViewById(R.id.checkBox3);
        checked = DataReader.checkComplexity(actionType - 1, 2, this);
        if (chk.isChecked() != checked)
            chk.setChecked(checked);

        chk = (CheckBox) findViewById(R.id.checkBox4);
        checked = DataReader.checkComplexity(actionType - 1, 3, this);
        if (chk.isChecked() != checked)
            chk.setChecked(checked);

        TextView complType = (TextView) findViewById(R.id.complTextView);
        switch (actionType) {
            case 1:
                complType.setText("Сложение");
                break;
            case 2:
                complType.setText("Вычитание");
                break;
            case 3:
                complType.setText("Умножение");
                break;
            case 4:
                complType.setText("Деление");
//                CheckBox chknew = (CheckBox) findViewById(R.id.checkBox5);
//                TextView twnew = (TextView) findViewById(R.id.textView7);
//                if (DataReader.checkComplexity(actionType - 1, 4, this)) {
//                    chknew.setChecked(check_old);
//                }
//                chknew.setText("275 : 55");
//                chknew.setId(R.id.checkBox5);
//                twnew.setText("Деление трёхзначного числа на двузначное");
//                LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
                break;
            default:
                finish();
                break;
        }
        StupidTextFill(actionType);
    }

    private void StupidTextFill(int operation) {
        TextView txv;
        switch (actionType) {
            case 1:
                txv = (TextView) findViewById(R.id.textView11);
                txv.setText("Сложение в одном разряде, дроби одного порядка");
                txv = (TextView) findViewById(R.id.textView12);
                txv.setText("0,5" + ADD + "0,7\n" + "0,55" + ADD + "0,07\n" + "0,555" + ADD + "0,007");


                txv = (TextView) findViewById(R.id.textView21);
                txv.setText("Сложение в двух разрядах, дроби одного порядка");
                txv = (TextView) findViewById(R.id.textView22);
                txv.setText("5,5" + ADD + "7,7\n" + "5,55" + ADD + "0,77\n" + "0,555" + ADD + "0,077");

                txv = (TextView) findViewById(R.id.textView31);
                txv.setText("Сложение в одном разряде, дроби разных порядков");
                txv = (TextView) findViewById(R.id.textView32);
                txv.setText("5" + ADD + "7,7\n" + "5,5" + ADD + "0,77\n" + "0,5" + ADD + "0,777");

                txv = (TextView) findViewById(R.id.textView41);
                txv.setText("Сложение в двух разрядах, дроби разных порядков");
                txv = (TextView) findViewById(R.id.textView42);
                txv.setText("55" + ADD + "77,7\n" + "55,5" + ADD + "7,77\n" + "0,55" + ADD + "0,777");

                break;
            case 2:
                txv = (TextView) findViewById(R.id.textView11);
                txv.setText("Вычитание в одном разряде, дроби одного порядка");
                txv = (TextView) findViewById(R.id.textView12);
                txv.setText("0,7" + SUB + "0,5\n" + "0,77" + SUB + "0,05\n" + "0,777" + SUB + "0,005");

                txv = (TextView) findViewById(R.id.textView21);
                txv.setText("Вычитание в двух разрядах, дроби одного порядка");
                txv = (TextView) findViewById(R.id.textView22);
                txv.setText("7,7" + SUB + "5,5\n" + "7,77" + SUB + "0,55\n" + "0,777" + SUB + "0,055");

                txv = (TextView) findViewById(R.id.textView31);
                txv.setText("Вычитание в одном разряде, дроби разных порядков");
                txv = (TextView) findViewById(R.id.textView32);
                txv.setText("7,7" + SUB + "5\n" + "0,77" + SUB + "0,7\n" + "7,77" + SUB + "0,005");

                txv = (TextView) findViewById(R.id.textView41);
                txv.setText("Вычитание в двух разрядах, дроби разных порядков");
                txv = (TextView) findViewById(R.id.textView42);
                txv.setText("77,7" + SUB + "55\n" + "77,7" + SUB + "0,55\n" + "7,77" + SUB + "0,055");
                break;
            case 3:
                txv = (TextView) findViewById(R.id.textView11);
                txv.setText("Умножение на десятичную дробь с единичной базой");
                txv = (TextView) findViewById(R.id.textView12);
                txv.setText("550" + MUL + "0,001\n" + "5,5" + MUL + "0,01\n" + "0,55" + MUL + "0,1");

                txv = (TextView) findViewById(R.id.textView21);
                txv.setText("Умножение двух чисел и десятичных дробей c однозначной базой");
                txv = (TextView) findViewById(R.id.textView22);
                txv.setText("500" + MUL + "0,005\n" + "5" + MUL + "0,05\n" + "0,5" + MUL + "0,5");

                txv = (TextView) findViewById(R.id.textView31);
                txv.setText("Умножение числа и десятичной дроби с двузначной и однозначной базой");
                txv = (TextView) findViewById(R.id.textView32);
                txv.setText("550" + MUL + "0,005\n" + "5,5" + MUL + "0,05\n" + "0,55" + MUL + "0,5");

                txv = (TextView) findViewById(R.id.textView41);
                txv.setText("Умножение числа и десятичной дроби с трёхзначной и однозначной базой");
                txv = (TextView) findViewById(R.id.textView42);
                txv.setText("555" + MUL + "0,005\n" + "5,55" + MUL + "0,05\n" + "0,555" + MUL + "0,5");
                break;
            case 4:
                txv = (TextView) findViewById(R.id.textView11);
                txv.setText("Деление на число с единичной базой");
                txv = (TextView) findViewById(R.id.textView12);
                txv.setText("500" + DIV + "1000\n" + "5,5" + DIV + "10\n" + "0,555" + DIV + "0,01");

                txv = (TextView) findViewById(R.id.textView21);
                txv.setText("Деление числа с двузначной базой на число с однозначной базой");
                txv = (TextView) findViewById(R.id.textView22);
                txv.setText("320" + DIV + "400\n" + "3,2" + DIV + "4\n" + "0,032" + DIV + "0,04");

                txv = (TextView) findViewById(R.id.textView31);
                txv.setText("Деление числа с трёхзначной базой на число с однозначной базой");
                txv = (TextView) findViewById(R.id.textView32);
                txv.setText("1280" + DIV + "4000\n" + "1,28" + DIV + "40\n" + "0,0128" + DIV + "0,4");

                txv = (TextView) findViewById(R.id.textView41);
                txv.setText("Деление числа с четырёхзначной базой на число с однозначной базой");
                txv = (TextView) findViewById(R.id.textView42);
                txv.setText("2048" + DIV + "4000\n" + "20,48" + DIV + "40\n" + "2,048" + DIV + "4");
                break;
            default:
                finish();
                break;
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
        CheckBox chk = (CheckBox) findViewById(R.id.checkBox1);
        if (chk.isChecked()) {
            currentComplexity.append("0").append(",");
        }
        chk = (CheckBox) findViewById(R.id.checkBox2);
        if (chk.isChecked()) {
            currentComplexity.append("1").append(",");
        }
        chk = (CheckBox) findViewById(R.id.checkBox3);
        if (chk.isChecked()) {
            currentComplexity.append("2").append(",");
        }
        chk = (CheckBox) findViewById(R.id.checkBox4);
        if (chk.isChecked()) {
            currentComplexity.append("3").append(",");
        }
//        if (actionType == 4) {
//            chk = (CheckBox) findViewById(R.id.checkBox5);
//            if (chk.isChecked()) {
//                currentComplexity.append("4").append(",");
//            }
//        }

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