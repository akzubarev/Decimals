package com.education4all.mathCoachAlg.tasks;

import android.util.Log;
import java.util.HashMap;

public class Fraction {
    private int _bottom;
    private int _integer;
    private int _top;

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

    public int get_integer() {
        return this._integer;
    }

    public int get_top() {
        return this._top;
    }

    public int get_bottom() {
        return this._bottom;
    }

    Fraction(int i, int t, int b) {
        if (i == -1 || t == -1 || b == -1 || b == 0) {
            this._integer = 0;
            this._top = 0;
            this._bottom = 1;
            Log.d("generatingexception", "-1 passed to Fraction");
            return;
        }
        this._integer = i;
        this._top = t;
        this._bottom = b;
    }

    public String toString() {
        String res = "";
        if (this._integer != 0) {
            res = res + this._integer;
        }
        if (this._top == 0) {
            return res;
        }
        return res + makeFraction(Integer.toString(this._top), Integer.toString(this._bottom));
    }

    public static String makeFraction(String top, String bottom) {
        if ((top + bottom).length() == 0) {
            return "";
        }
        StringBuilder res = new StringBuilder();
        for (char sym : top.toCharArray()) {
            res.append(uppersyms.get(Character.valueOf(sym)));
        }
        res.append(slash);
        for (char sym2 : bottom.toCharArray()) {
            res.append(lowersyms.get(Character.valueOf(sym2)));
        }
        return res.toString();
    }

    public static String makeValue(String integer, String top, String bottom) {
        return integer + makeFraction(top, bottom);
    }

    public static String makeValue(int integer, int top, int bottom) {
        return new Fraction(integer, top, bottom).simplify().toString();
    }

    private int gcd(int p, int q) {
        if (q == 0) {
            return p;
        }
        return gcd(q, p % q);
    }

    public Fraction remove_integer_part() {
        int i = this._integer;
        int i2 = this._top;
        int i3 = this._bottom;
        this._integer = i + (i2 / i3);
        this._top = i2 % i3;
        return this;
    }

    public Fraction simplify() {
        int i = this._integer;
        int i2 = this._top;
        int i3 = this._bottom;
        this._integer = i + (i2 / i3);
        int i4 = i2 % i3;
        this._top = i4;
        int gcd = gcd(i4, i3);
        this._top /= gcd;
        this._bottom /= gcd;
        return this;
    }

    public boolean more(Fraction other) {
        int i = this._integer - other._integer;
        int i2 = this._bottom;
        int i3 = other._bottom;
        return (((i * i2) * i3) + (this._top * i3)) - (other._top * i2) > 0;
    }

    public boolean more(int other) {
        int i = this._integer;
        int i2 = this._bottom;
        return (i * i2) + this._top > i2 * other;
    }

    public Fraction plus(Fraction other) {
        int new_integer = this._integer + other._integer;
        int i = this._top;
        int i2 = other._bottom;
        int i3 = other._top;
        int i4 = this._bottom;
        return new Fraction(new_integer, (i * i2) + (i3 * i4), i4 * i2).simplify();
    }

    public Fraction minus(Fraction other) {
        int i = this._integer - other._integer;
        int i2 = this._bottom;
        int i3 = other._bottom;
        return new Fraction(0, (((i * i2) * i3) + (this._top * i3)) - (other._top * i2), i2 * i3).simplify();
    }

    public Fraction mult(Fraction other) {
        int i = this._integer;
        int i2 = this._bottom;
        int i3 = (i * i2) + this._top;
        int i4 = other._integer;
        int i5 = other._bottom;
        return new Fraction(0, i3 * ((i4 * i5) + other._top), i2 * i5).simplify();
    }

    public Fraction div(Fraction other) {
        int i = this._integer;
        int i2 = this._bottom;
        int i3 = (i * i2) + this._top;
        int i4 = other._bottom;
        return new Fraction(0, i3 * i4, ((other._integer * i4) + other._top) * i2).simplify();
    }

    public boolean equals(Fraction other) {
        return this._integer == other._integer && this._top == other._top && this._bottom == other._bottom;
    }

    public static Fraction null_element() {
        return new Fraction(0, 0, 1);
    }

    public void to_int() {
        this._top = 0;
        this._bottom = 1;
    }
}
