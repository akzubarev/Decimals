package com.education4all.MathCoachAlg.Tasks;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class FractionTask extends Task {

    //    String integer1, integer2, integerA,
//            top1, top2, topA,
//            bottom1, bottom2, bottomA;
//    SpannableString fraction1, fraction2, fractionA;
//    String operationSym;
    private final static String slash = "⁄";
    private final static HashMap<Character, Character> uppersyms = new HashMap<>();
    private final static HashMap<Character, Character> lowersyms = new HashMap<>();

    static {
        uppersyms.put('1', '¹');
        uppersyms.put('2', '²');
        uppersyms.put('3', '³');
        uppersyms.put('4', '⁴');
        uppersyms.put('5', '⁵');
        uppersyms.put('6', '⁶');
        uppersyms.put('7', '⁷');
        uppersyms.put('8', '⁸');
        uppersyms.put('9', '⁹');
        uppersyms.put('0', '⁰');

        lowersyms.put('1', '₁');
        lowersyms.put('2', '₂');
        lowersyms.put('3', '₃');
        lowersyms.put('4', '₄');
        lowersyms.put('5', '₅');
        lowersyms.put('6', '₆');
        lowersyms.put('7', '₇');
        lowersyms.put('8', '₈');
        lowersyms.put('9', '₉');
        lowersyms.put('0', '₀');
    }

    public FractionTask() {
//        integer1 = String.valueOf(3);
//        top1 = String.valueOf(1);
//        bottom1 = String.valueOf(2);
//
//        integer2 = String.valueOf(2);
//        top2 = String.valueOf(1);
//        bottom2 = String.valueOf(3);
//
//        integerA = String.valueOf(5);
//        topA = String.valueOf(5);
//        bottomA = String.valueOf(6);
//
//        fraction1 = makeFraction(top1, bottom1);
//        fraction2 = makeFraction(top2, bottom2);
//        fractionA = makeFraction(topA, bottomA);
//        operationSym = operations[0];
//
//        expression = makeValue(integer1, top1, bottom1) + " " + operationSym + " " + makeValue(integer2, top2, bottom2);
//        answer = makeValue(integerA, topA, bottomA);
        expression = makeValue("3", "1", "2") + operations[0]
                + makeValue("2", "1", "3");
        answer = makeValue("5", "5", "6");

        userAnswer = "";
        rnd = new Random();

    }

//    public boolean checkanswer(String integer, String top, String bottom) {
//        return integer.equals(integerA) && top.equals(topA) && bottom.equals(bottomA);
//    }

//    public String[] getAnswer() {
//        return new String[]{integerA, topA, bottomA};
//    }
//
//    public String[] getIntegers() {
//        return new String[]{integer1, integer2, integerA};
//    }
//
//    public SpannableString[] getFractions() {
//        return new SpannableString[]{fraction1, fraction2, fractionA};
//    }
//
//    public String getOperation() {
//        return operationSym;
//    }

//    public static SpannableString makeFraction(String top, String bottom) {
////        return new SpannableString(Html.fromHtml(String.format("<sup>%s</sup>/<sub>%s</sub>", top, bottom)));
//        if ((top + bottom).length() == 0)
//            return SpannableString.valueOf("");
//
//        SpannableString res = new SpannableString(top + "/" + bottom);
//
//        res.setSpan(new SuperscriptSpan(), 0, top.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        res.setSpan(new SubscriptSpan(), top.length() + 1, res.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return res;
//    }

    public static String makeFraction(String top, String bottom) {
//        return new SpannableString(Html.fromHtml(String.format("<sup>%s</sup>/<sub>%s</sub>", top, bottom)));
        if ((top + bottom).length() == 0)
            return "";
        String res = "";

        for (char sym : top.toCharArray())
            res += uppersyms.get(sym);
        res += slash;
        for (char sym : bottom.toCharArray())
            res += lowersyms.get(sym);
        return res;
    }

//    public void workWithFractions() {
//        TextView decimal = findViewById(R.id.decimal);
//        TextView integer = findViewById(R.id.integer);
//        SpannableString integertext = new SpannableString("3");
//        integertext = resizeeverythingRel(integertext, 2);
//        integer.setText(integertext);
//        decimal.setText(Html.fromHtml("<sup>1</sup>/<sub>2</sub>"), TextView.BufferType.SPANNABLE);
//    }
//
//    public SpannableString resizeeverythingRel(SpannableString spannable, int increase) { spannable.setSpan(new SizeSpan(increase), 0, spannable.length(), 0);
//        return spannable;
//    }
//
//    public SpannableString resizefractionRel(SpannableString spannable, String target, int increase) {
//
//        int startindex = spannable.toString().indexOf(target);
//        spannable.setSpan(new RelativeSizeSpan(increase), startindex, startindex + target.length(), 0);
//        return spannable;
//    }


//    public SpannableStringBuilder makeExpression() {
//        SpannableStringBuilder expr = new SpannableStringBuilder();
//        expr.append(integer1).append(fraction1).append(operationSym)
//                .append(integer2).append(fraction2).append(" = ");
//        return expr;
//
//    }

    public static String makeValue(String integer, String top, String bottom) {
        //return integer + "[" + makeFraction(top, bottom) + "]";
        return integer + makeFraction(top, bottom);
    }

//    void breakTask() {
//        int idx1, idx2, idx3;
//        String[] parts = expression.split(" ");
//        String part;
//        String fraction;
//
//        part = parts[0];
//        idx1 = part.indexOf("[");
//        idx2 = part.indexOf("]");
//        integer1 = part.substring(0, idx1);
//        fraction = part.substring(idx1 + 1, idx2);
//        idx3 = fraction.indexOf("/");
//        fraction1 = makeFraction(fraction.substring(0, idx3), fraction.substring(idx3 + 1));
//
//        part = parts[2];
//        idx1 = part.indexOf("[");
//        idx2 = part.indexOf("]");
//        integer2 = part.substring(0, idx1);
//        fraction = part.substring(idx1 + 1, idx2);
//        idx3 = fraction.indexOf("/");
//        fraction2 = makeFraction(fraction.substring(0, idx3), fraction.substring(idx3 + 1));
//
//        part = answer;
//        idx1 = part.indexOf("[");
//        idx2 = part.indexOf("]");
//        integerA = part.substring(0, idx1);
//        fraction = part.substring(idx1 + 1, idx2);
//        idx3 = fraction.indexOf("/");
//        fractionA = makeFraction(fraction.substring(0, idx3), fraction.substring(idx3 + 1));
//
//        operationSym = operations[operation];
//    }

    public FractionTask(String line) {
        int found = line.indexOf(';');
        expression = line.substring(0, found);
        line = line.substring(found + 1);
        found = line.indexOf(';');
        operation = Integer.parseInt(line.substring(0, found));
        line = line.substring(found + 1);
        found = line.indexOf(';');
        complexity = Integer.parseInt(line.substring(0, found));
        line = line.substring(found + 1);
        found = line.indexOf(';');
        answer = line.substring(0, found);
        line = line.substring(found + 1);
        found = line.indexOf(';');
        userAnswer = line.substring(0, found);
        line = line.substring(found + 1);
        found = line.indexOf(';');
        taskTime = Long.parseLong(line.substring(0, found));
        line = line.substring(found + 1);
        found = line.indexOf(';');
        timeTaken = Long.parseLong(line.substring(0, found));

        // breakTask();
    }

    @Override
    public void generate(int[][] allowedTasks) {
        if (areTasks(allowedTasks)) {
            operation = operationRandomizer(allowedTasks);
            switch (operation) {
                case 0:
                    addition(allowedTasks);
                    break;
                case 1:
                    substraction(allowedTasks);
                    break;
                case 2:
                    multiplication(allowedTasks);
                    break;
                case 3:
                    division(allowedTasks);
                    break;
            }
            taskTime = System.currentTimeMillis();
        }
    }


    // +
    private void addition(final int[][] allowedTasks) {

    }

    // -
    private void substraction(final int[][] allowedTasks) {

    }

    // *
    private void multiplication(final int[][] allowedTasks) {

    }

    // :
    private void division(final int[][] allowedTasks) {
    }

}
