package com.education4all.MathCoachAlg.Tasks;

import java.util.HashMap;

public class Fraction {
    private int _integer, _top, _bottom;

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

    Fraction(int i, int t, int b) {
        _integer = i;
        _top = t;
        _bottom = b;
    }

    public String toString() {
        String res = "";
        if (_integer != 0)
            res += _integer;
        if (_top != 0)
            res += makeFraction(Integer.toString(_top), Integer.toString(_bottom));
        return res;
    }

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

    public static String makeValue(String integer, String top, String bottom) {
        //return integer + "[" + makeFraction(top, bottom) + "]";
        return integer + makeFraction(top, bottom);
    }

    public static String makeValue(int integer, int top, int bottom) {
        //return integer + "[" + makeFraction(top, bottom) + "]";
        Fraction fraction = new Fraction(integer, top, bottom);
        fraction = fraction.simplify();
        return fraction.toString();
    }

    public Fraction simplify() {

        _integer += _top / _bottom;
        _top = _top % _bottom;

        boolean change = false;
        do {
            int limit = Math.min(_top, _bottom);
            change = false;
            for (int i = 2; i <= limit; i++) {
                if (_top % i == 0 && _bottom % i == 0) {
                    _top /= i;
                    _bottom /= i;
                    change = true;
                    break;
                }
            }
        } while (change);
        return this;
    }

    public boolean more(Fraction other) {

        return (_integer - other._integer) * _bottom * other._bottom
                + _top * other._bottom - other._top * _bottom > 0;
    }


    public Fraction plus(Fraction other) {
        int new_integer = _integer + other._integer;
        int new_top = _top * other._bottom + other._top * _bottom;

        int new_bottom = _bottom * other._bottom;
        return new Fraction(new_integer, new_top, new_bottom).simplify();
    }

    public Fraction minus(Fraction other) {
        int new_top = (_integer - other._integer) * _bottom * other._bottom +
                _top * other._bottom - other._top * _bottom;
        int new_bottom = _bottom * other._bottom;
        return new Fraction(0, new_top, new_bottom).simplify();
    }

    public Fraction mult(Fraction other) {
        int new_top = (_integer * _bottom + _top) * (other._integer * other._bottom + other._top);
        int new_bottom = _bottom * other._bottom;
        return new Fraction(0, new_top, new_bottom).simplify();
    }

    public Fraction div(Fraction other) {
        int new_top = (_integer * _bottom + _top) * other._bottom;
        int new_bottom = (other._integer * other._bottom + other._top) * _bottom;
        return new Fraction(0, new_top, new_bottom).simplify();
    }

    public boolean equals(Fraction other) {
        return _integer == other._integer && _top == other._top && _bottom == other._bottom;

    }
}

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
