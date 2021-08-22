package com.education4all.mathCoachAlg.tasks;

import java.util.Locale;
import java.util.Random;

public class DecimalTask extends Task {

    public DecimalTask() {
        expression = "2 + 2";
        answer = "4";
        userAnswer = "";
    }

    public DecimalTask(String line) {
       deSerializeOld(line);
    }

    @Override
    public void generate() {
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

    String withPrecision(double value) {
        int precision = 0;
        double temp = value;
        int maxprecision = 10;
        double mindifference =  Math.pow(0.1, maxprecision);

        if (value - (int) value > mindifference)
            while (temp - (int) temp >mindifference && precision <= maxprecision) {
                temp *= 10;
                precision++;

                if (precision >= maxprecision || temp - (int) temp >= 1) {
                    int temp1 = (int) temp;
                    if (temp1 % 10 == 9)
                        temp1++;
                    while (temp1 % 10 == 0 && value - temp1 > mindifference) {
                        temp1 /= 10;
                        precision--;

                    }
                    break;
                }
            }

        if (precision == 0)
            return String.format(new Locale("ru", "RU"), "%d", Math.round(value));
        else {
            String out = String.format(new Locale("ru", "RU"), "%." + precision + "f", value);
            return trimZeroes(out);
        }

//        int log = (int) Math.log10(value);
//
//        long longdouble = (long) (value * Math.pow(10, 10));
//        if (longdouble % 10 == 9)
//            longdouble++;
//        while (longdouble % 10 == 0 && Math.log(longdouble) > log)
//            longdouble /= 10;
//
//        if (Math.log(longdouble) == log)
//            return String.format(new Locale("ru", "RU"), "%d", Math.round(value));
//        else {
//            String double_ = "" + longdouble;
//            String integer = double_.substring(0, log + 2);
//            if (integer.isEmpty())
//                integer = "0";
//            String decimal = double_.substring(log + 2, double_.length());
//            return integer + "." + decimal;
//        }

    }

    private String trimZeroes(String out) {
        for (int i = out.length() - 1; i >= 0; i--) {
            char c = out.charAt(i);
            if (c == ',')
                return out.substring(0, i);
            if (c != '0')
                return out.substring(0, i + 1);
        }
        return "0";
    }

    // +
    private void addition(final int[][] allowedTasks) {
        int n = 0;
        int m = 0;
        int minpow = 1;
        complexity = complexityRandomizer(allowedTasks);

        switch (complexity) {
            case 0:
                switch (randomInclusive(0, 2, false)) {
                    case 0:
                        n = rndtype(1);
                        break;
                    case 1:
                        n = rndtype(2);
                        break;
                    case 2:
                    default:
                        n = rndtype(3);
                        break;
                }
                m = rndtype(1);
                break;
            case 1:
                if (rnd.nextBoolean())
                    n = rndtype(2);
                else
                    n = rndtype(3);
                m = rndtype(2);
                break;
            case 2:
                if (rnd.nextBoolean())
                    switch (randomInclusive(0, 3, false)) {
                        case 0:
                            n = rndtype(1) * 10;
                            m = rndtype(2);
                            break;
                        case 1:
                            n = rndtype(1) * 10;
                            m = rndtype(3);
                            break;
                        case 2:
                            n = rndtype(2) * 10;
                            m = rndtype(2);
                            break;
                        case 3:
                        default:
                            n = rndtype(3) * 10;
                            m = rndtype(2);
                            break;
                    }
                else {
                    switch (randomInclusive(0, 1, false)) {
                        case 0:
                            n = rndtype(1) * 100;
                            break;
                        case 1:
                            n = rndtype(2) * 100;
                            break;
                        case 2:
                        default:
                            n = rndtype(3) * 100;
                            minpow = 2;
                            break;
                    }
                    m = rndtype(3);
                }
                break;
            case 3:
                if (rnd.nextBoolean()) {
                    n = rndtype(2) * 10;
                } else {
                    n = rndtype(3) * 10;
                }
                m = rndtype(3);
                break;
            default:
                ////DatasaveErrorLog("No valid complexity for addition, yet called");
                break;
        }

        if (rnd.nextBoolean()) {
            int k = n;
            n = m;
            m = k;
        }

        int pow = randomInclusive(minpow, 3, false);
        double div = Math.pow(10, pow);
        double nn = n / div, mm = m / div;

        expression = withPrecision(nn) + operations[0] + withPrecision(mm);
        answer = withPrecision(nn + mm);
    }

    // -
    private void substraction(final int[][] allowedTasks) {
        int m = 0;
        int n = 0;
        int minpow = 1;
        complexity = complexityRandomizer(allowedTasks);
        do
            switch (complexity) {
                case 0:
                    switch (randomInclusive(0, 2, false)) {
                        case 0:
                            n = rndtype(1);
                            break;
                        case 1:
                            n = rndtype(2);
                            break;
                        case 2:
                        default:
                            n = rndtype(3);
                            break;
                    }
                    m = rndtype(1);
                    break;
                case 1:
                    if (rnd.nextBoolean())
                        n = rndtype(2);
                    else
                        n = rndtype(3);

                    m = rndtype(2);
                    break;
                case 2:
                    switch (randomInclusive(0, 4, false)) {
                        case 0:
                            switch (randomInclusive(0, 2, false)) {
                                case 0:
                                    n = rndtype(1) * 10;
                                    m = rndtype(1);
                                    break;
                                case 1:
                                    n = rndtype(2) * 10;
                                    m = rndtype(1);
                                    break;
                                case 2:
                                default:
                                    n = rndtype(3) * 10;
                                    m = rndtype(1);
                                    minpow = 2;
                                    break;
                            }
                            break;
                        case 1:
                            if (rnd.nextBoolean())
                                n = rndtype(1) * 100;
                            else
                                n = rndtype(2) * 100;
                            m = rndtype(1);
                            minpow = 2;
                            break;
                        case 2:
                            n = rndtype(1) * 1000;
                            m = rndtype(1);
                            minpow = 2;
                            break;
                        case 3:
                            if (rnd.nextBoolean())
                                n = rndtype(2);
                            else
                                n = rndtype(3);
                            m = rndtype(1) * 10;
                            break;
                        case 4:
                        default:
                            n = rndtype(3);
                            m = rndtype(1) * 100;
                            break;
                    }
                    break;
                case 3:
                    switch (randomInclusive(0, 3, false)) {
                        case 0:
                            switch (randomInclusive(0, 2, false)) {
                                case 0:
                                    n = rndtype(1) * 10;
                                    m = rndtype(2);
                                    break;
                                case 1:
                                    n = rndtype(2) * 10;
                                    m = rndtype(2);
                                    break;
                                case 2:
                                default:
                                    n = rndtype(3) * 10;
                                    m = rndtype(2);
                                    minpow = 2;
                                    break;
                            }
                            break;
                        case 1:
                            switch (randomInclusive(0, 2, false)) {
                                case 0:
                                    n = rndtype(1) * 100;
                                    minpow = 2;
                                    break;
                                case 1:
                                    n = rndtype(2) * 100;
                                    minpow = 2;
                                    break;
                                case 2:
                                    n = rndtype(3) * 100;
                                    minpow = 3;
                                    break;
                            }
                            m = rndtype(2);
                            break;
                        case 2:
                            if (rnd.nextBoolean())
                                n = rndtype(1) * 1000;
                            else
                                n = rndtype(2) * 1000;
                            m = rndtype(2);
                            minpow = 2;
                            break;
                        case 3:
                        default:
                            n = rndtype(3);
                            m = rndtype(2) * 10;
                            break;
                    }
                    break;
                default:
                    ////DatasaveErrorLog("No valid complexity for substraction, yet called");
                    break;
            }
        while (n <= m);

        int pow = randomInclusive(minpow, 3, false);
        double div = Math.pow(10, pow);
        double nn = n / div, mm = m / div;

        expression = withPrecision(nn) + operations[1] + withPrecision(mm);
        answer = withPrecision(nn - mm);
    }

    // *
    private void multiplication(final int[][] allowedTasks) {
        int n = 0;
        int m = 0;
        complexity = complexityRandomizer(allowedTasks);
        int pown = 0, powm = 0;
        double nn, mm, divn, divm;

        do {
            switch (complexity) {
                case 0:
                    int temp = randomInclusive(1, 3, false);
                    n = rndtype(temp);
                    m = 1;

                    pown = randomInclusive(-(3 - temp), 3, false);
                    powm = randomInclusive(1, Math.min(4 - pown, 3), false);
                    break;
                case 1:
                    n = randomInclusive(2, 9, true);
                    m = randomInclusive(2, 9, true);
                    do {
                        pown = randomInclusive(-2, 3, false);
                        powm = randomInclusive(-2, Math.min(4 - pown, 3), false);
                    } while (pown < 1 && powm < 1);
                    break;
                case 2:
                    n = rndtype(2);
                    m = randomInclusive(2, 9, true);
                    do {
                        pown = randomInclusive(-1, 3, false);
                        powm = randomInclusive(-2, Math.min(4 - pown, 3), false);
                    } while (pown < 1 && powm < 1);
                    break;
                case 3:
                    n = rndtype(3);
                    m = randomInclusive(2, 9, true);
                    do {
                        pown = randomInclusive(0, 3, false);
                        powm = randomInclusive(-2, Math.min(4 - pown, 3), false);
                    } while (pown < 1 && powm < 1);
                    break;
                default:
                    //DatasaveErrorLog("No valid complexity for multiplication, yet called");
                    break;
            }

            divn = Math.pow(10, pown);
            divm = Math.pow(10, powm);
            nn = n / divn;
            mm = m / divm;
        }
        while (nn == 1 || mm == 1 || ((int) nn == nn && (int) mm == mm));

        if (rnd.nextBoolean()) {
            double k = nn;
            nn = mm;
            mm = k;
        }
        expression = withPrecision(nn) + operations[2] + withPrecision(mm);
        answer = withPrecision(nn * mm);
    }

    // :
    private void division(final int[][] allowedTasks) {
        int n = 0, m = 0, k = 0;
        complexity = complexityRandomizer(allowedTasks);
        int pown = 0, powm = 0, powk = 0;
        double nn = 0, mm = 0, kk = 0, divn = 0, divm = 0, divk = 0;

        switch (complexity) {
            case 0:
                int temp = randomInclusive(1, 3, false);
                n = rndtype(temp);
                m = 1;
                do {
                    pown = randomInclusive(temp - 3, 3, false);
                    powm = randomInclusive(-3, 3, true);
                    divn = Math.pow(10, pown);
                    divm = Math.pow(10, powm);
                    nn = n / divn;
                    mm = m / divm;
                    kk = nn / mm;
                } while ((int) nn == nn && (int) mm == mm && (int) kk == kk);
                break;
            case 1:
                k = rndtype(1);
                m = randomInclusive(2, 9, false);
                do {
                    powk = randomInclusive(-3, 3, false);
                    powm = randomInclusive(Math.max(-3, -2 - powk), Math.min(3, 3 - powk), false);
                    divk = Math.pow(10, powk);
                    divm = Math.pow(10, powm);
                    kk = k / divk;
                    mm = m / divm;
                    nn = kk * mm;
                } while ((int) nn == nn && (int) mm == mm && (int) kk == kk);
                break;
            case 2:
                k = rndtype(2);
                m = randomInclusive(2, 9, false);
                do {
                    powk = randomInclusive(-3, 3, false);
                    powm = randomInclusive(Math.max(-3, -1 - powk), Math.min(3, 3 - powk), false);
                    divk = Math.pow(10, powk);
                    divm = Math.pow(10, powm);
                    kk = k / divk;
                    mm = m / divm;
                    nn = kk * mm;
                } while ((int) nn == nn && (int) mm == mm && (int) kk == kk);
                break;
            case 3:
                k = rndtype(3);
                m = randomInclusive(2, 9, false);
                do {
                    powk = randomInclusive(-3, 3, false);
                    powm = randomInclusive(Math.max(-3, -powk), Math.min(3, 3 - powk), false);
                    divk = Math.pow(10, powk);
                    divm = Math.pow(10, powm);
                    kk = k / divk;
                    mm = m / divm;
                    nn = kk * mm;
                } while ((int) nn == nn && (int) mm == mm && (int) kk == kk);
                break;
        }
        expression = withPrecision(nn) + operations[3] + withPrecision(mm);
        answer = withPrecision(kk);
    }

}
