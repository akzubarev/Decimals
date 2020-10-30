package com.education4all.decimals;

import android.content.SharedPreferences;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

        ActionBar myActionBar = getSupportActionBar();
        myActionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        actionType = extras.getInt("Type");
        if (actionType != 4) {
            CheckBox chk1 = (CheckBox) findViewById(R.id.checkBox5);
            ((ViewManager) chk1.getParent()).removeView(chk1);
            TextView tw1 = (TextView)findViewById(R.id.textView7);
            ((ViewManager) tw1.getParent()).removeView(tw1);
        }
        boolean check  = false;
        CheckBox chk = (CheckBox)findViewById(R.id.checkBox1);
        if (DataReader.checkComplexity(actionType - 1, 0, this)) {
            chk.setChecked(true);
        }
        chk = (CheckBox)findViewById(R.id.checkBox2);
        if (DataReader.checkComplexity(actionType - 1, 1, this)) {
            chk.setChecked(true);
        }
        chk = (CheckBox)findViewById(R.id.checkBox5);
        if (actionType == 4 && DataReader.checkComplexity(actionType - 1, 4, this)) {
            check = true;
        }
        chk = (CheckBox)findViewById(R.id.checkBox3);
        if (DataReader.checkComplexity(actionType - 1, 2, this)) {
            chk.setChecked(true);
        }
        chk = (CheckBox)findViewById(R.id.checkBox4);
        if (DataReader.checkComplexity(actionType - 1, 3, this)) {
            chk.setChecked(true);
        }

        TextView complType = (TextView)findViewById(R.id.complTextView);
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
                CheckBox chknew = (CheckBox) findViewById(R.id.checkBox5);
                TextView twnew = (TextView)findViewById(R.id.textView7);
                if (DataReader.checkComplexity(actionType - 1, 4, this)) {
                    chknew.setChecked(check);
                }
                chknew.setText("275 : 55");
                chknew.setId(R.id.checkBox5);
                twnew.setText("Деление трёхзначного числа на двузначное");
                LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
                break;
            default:
                finish();
                break;
        }
        StupidTextFill(actionType);
    }

    private void StupidTextFill(int operation) {
        CheckBox chk;
        TextView txv;
        switch (actionType) {
            case 1:
                chk = (CheckBox)findViewById(R.id.checkBox1);
                chk.setText("5" + ADD + "7");
                txv = (TextView)findViewById(R.id.textView3);
                txv.setText("Сложение в пределах однозначных чисел");
                chk = (CheckBox)findViewById(R.id.checkBox2);
                chk.setText("55" + ADD + "77");
                txv = (TextView)findViewById(R.id.textView4);
                txv.setText("Сложение в пределах двузначных чисел");
                chk = (CheckBox)findViewById(R.id.checkBox3);
                chk.setText("555" + ADD + "777");
                txv = (TextView)findViewById(R.id.textView5);
                txv.setText("Сложение в пределах трёхзначных чисел");
                chk = (CheckBox)findViewById(R.id.checkBox4);
                chk.setText("5555" + ADD + "7777");
                txv = (TextView)findViewById(R.id.textView6);
                txv.setText("Сложение в пределах четырёхзначных чисел");
                break;
            case 2:
                chk = (CheckBox)findViewById(R.id.checkBox1);
                chk.setText("7" + SUB + "5");
                txv = (TextView)findViewById(R.id.textView3);
                txv.setText("Вычитание в пределах однозначных чисел");
                chk = (CheckBox)findViewById(R.id.checkBox2);
                chk.setText("77" + SUB + "55");
                txv = (TextView)findViewById(R.id.textView4);
                txv.setText("Вычитание в пределах двузначных чисел");
                chk = (CheckBox)findViewById(R.id.checkBox3);
                chk.setText("777" + SUB + "555");
                txv = (TextView)findViewById(R.id.textView5);
                txv.setText("Вычитание в пределах трёхзначных чисел");
                chk = (CheckBox)findViewById(R.id.checkBox4);
                chk.setText("7777" + SUB + "5555");
                txv = (TextView)findViewById(R.id.textView6);
                txv.setText("Вычитание в пределах четырёхзначных чисел");
                break;
            case 3:
                chk = (CheckBox)findViewById(R.id.checkBox1);
                chk.setText("5" + MUL + "5");
                txv = (TextView)findViewById(R.id.textView3);
                txv.setText("Умножение однозначных чисел");
                chk = (CheckBox)findViewById(R.id.checkBox2);
                chk.setText("5" + MUL + "25");
                txv = (TextView)findViewById(R.id.textView4);
                txv.setText("Умножение двузначного числа на однозначное");
                chk = (CheckBox)findViewById(R.id.checkBox3);
                chk.setText("5" + MUL + "525");
                txv = (TextView)findViewById(R.id.textView5);
                txv.setText("Умножение трёхзначного числа на однозначное");
                chk = (CheckBox)findViewById(R.id.checkBox4);
                chk.setText("25" + MUL + "25");
                txv = (TextView)findViewById(R.id.textView6);
                txv.setText("Умножение двузначных чисел");
                break;
            case 4:
                chk = (CheckBox)findViewById(R.id.checkBox1);
                chk.setText("25" + DIV + "5");
                txv = (TextView)findViewById(R.id.textView3);
                txv.setText("Деление двузначного числа на однозначное");
                chk = (CheckBox)findViewById(R.id.checkBox2);
                chk.setText("275" + DIV + "5");
                txv = (TextView)findViewById(R.id.textView4);
                txv.setText("Деление трёхзначного числа на однозначное");
                chk = (CheckBox)findViewById(R.id.checkBox3);
                chk.setText("2775" + DIV + "5");
                txv = (TextView)findViewById(R.id.textView5);
                txv.setText("Деление четырёхзначного числа на однозначное");
                chk = (CheckBox)findViewById(R.id.checkBox4);
                chk.setText("3025" + DIV + "25");
                txv = (TextView)findViewById(R.id.textView6);
                txv.setText("Деление четырёхзначного числа на двузначное");
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
        CheckBox chk = (CheckBox)findViewById(R.id.checkBox1);
        if (chk.isChecked()) {
            currentComplexity.append("0").append(",");
        }
        chk = (CheckBox)findViewById(R.id.checkBox2);
        if (chk.isChecked()) {
            currentComplexity.append("1").append(",");
        }
        chk = (CheckBox)findViewById(R.id.checkBox3);
        if (chk.isChecked()) {
            currentComplexity.append("2").append(",");
        }
        chk = (CheckBox)findViewById(R.id.checkBox4);
        if (chk.isChecked()) {
            currentComplexity.append("3").append(",");
        }
        if (actionType == 4) {
            chk = (CheckBox)findViewById(R.id.checkBox5);
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