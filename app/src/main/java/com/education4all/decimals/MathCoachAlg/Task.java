package com.education4all.decimals.MathCoachAlg;

import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;


public class Task {
    public String expression;
    public int operation;
    public int complexity;
    public String answer;
    public String userAnswer;
    public long taskTime;
    public long timeTaken;
    public Random rnd;
    public static final String[] operations = {"\u2006+\u2006", "\u2006−\u2006", "\u2006\u22C5\u2006", "\u2006:\u2006"};

    public Task() {
        expression = "2 + 2";
        answer = "4";
        userAnswer = "";
        rnd = new Random();
    }

//	    public Task(final int allowedTasks[][]) {
//	        operation = operationRandomizer(allowedTasks);
//	        switch (operation) {
//	            case 0:
//	                addition(allowedTasks);
//	            break;
//	            case 1:
//	                substraction(allowedTasks);
//	            break;
//	            case 2:
//	                multiplication(allowedTasks);
//	            break;
//	            case 3:
//	                division(allowedTasks);
//	            break;
//	        }
//	        taskTime = Calendar.getInstance().getTimeInMillis();
//		}

    public static String DepictTask(String line) {
        Task l_task = new Task(line);
//		return l_task.expression + " = " + l_task.userAnswer + "(" + l_task.answer + ") " + l_task.timeTaken + " сек. ";
        return l_task.expression + " = " + l_task.userAnswer + " " + l_task.timeTaken + " сек. ";
    }

    public static ArrayList<String> DepictTaskExtended(String line, ArrayList<String> answers) {
        Task l_task = new Task(line);
        ArrayList<String> res = new ArrayList<String>();
        String firstPart = l_task.expression + " = ";
        String partToBeParsed = l_task.userAnswer;
        int commaIndex;
        int colonIndex;
        String oneAnswer;
        String oneAnswerTime;
        while (partToBeParsed.length() > 0) {
            commaIndex = partToBeParsed.indexOf('|');
            colonIndex = partToBeParsed.indexOf(':');
            if (colonIndex > 0) {
                oneAnswer = partToBeParsed.substring(0, colonIndex);
                oneAnswerTime = partToBeParsed.substring(colonIndex + 1, commaIndex);
                res.add(firstPart + oneAnswer + "  (" + oneAnswerTime + " сек)");
                answers.add(oneAnswer);
            }
            partToBeParsed = partToBeParsed.substring(commaIndex + 1);
        }
        return res;
    }

//	public static ArrayList<String> DepictTaskExpanded(String line) {
//		Task l_task = new Task(line);
//		ArrayList<String> result = new ArrayList<String>();
//		String answers = l_task.userAnswer;
//		int coma = 0;
//		coma = answers.indexOf(',');
//		while (coma > 0) {
//			result.add(l_task.userAnswer.substring(coma - 1, coma));
//			answers = answers.substring(coma + 1);
//			coma = answers.indexOf(',');
//		}
//		return result;
//	}


    public Task(String line) {
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
    }

    public void generate(final int[][] allowedTasks) {
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

    public boolean areTasks(int[][] p_allowedTasks) {
        for (int i = 0; i < p_allowedTasks.length; ++i) {
            if (p_allowedTasks[i].length > 0) {
                return true;
            }
        }
        return false;
    }

    public String serialize() {
        return expression + ";" + operation + ";" + complexity + ";" +
                answer + ";" + userAnswer + ";" + taskTime + ";" +
                timeTaken + ";";
    }

    public void deSerialize(String line) {
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
        taskTime = Integer.parseInt(line.substring(0, found));
        line = line.substring(found + 1);
        found = line.indexOf(';');
        timeTaken = Integer.parseInt(line.substring(0, found));
    }

//	    public String getOperation(int n) {
//	    	return operations[n];
//	    }

    // ����� ������ ��������
    private int operationRandomizer(final int[][] allowedTasks) {
        int c = 0;
        int i;
        while (true) {
            ++c;
            i = rnd.nextInt(allowedTasks.length);
            if (allowedTasks[i].length > 0) {
                return i;
            }
        }
    }

    // ����� ��������� �������
    private int complexityRandomizer(final int[][] allowedTasks) {
        if (allowedTasks[operation].length > 0) {
            int r = rnd.nextInt(allowedTasks[operation].length);
            return allowedTasks[operation][r];
        } else {
            return -1;
        }
    }

    int randomInclusive(int left, int right, boolean nozeroes) {
        int result = rnd.nextInt(right - left + 1) + left;

        if (nozeroes)
            while (result % 10 == 0)
                result = rnd.nextInt(right - left + 1) + left;

        return result;
    }

    int rndtype(int type) {
        switch (type) {
            case 1:
            default:
                return randomInclusive(1, 9, true);
            case 2:
                return randomInclusive(11, 99, true);
            case 3:
                return randomInclusive(101, 999, true);
        }
    }

    String withPrecision(double value) {
        int precision = 0;
        double temp = value;

        if (value - (int) value > 0.00001)
            while (temp - (int) temp > 0.00001) {
                temp *= 10;
                precision++;

                if (precision >= 5 || temp - (int) temp >= 1) {
                    int temp1 = (int) temp;
                    if (temp1 % 10 == 9)
                        temp1++;
                    while (temp1 % 10 == 0 && temp1 > value - 0.00001) {
                        temp1 /= 10;
                        precision--;

                    }

//                    Log.d("EndlessLoop", String.format("%d", temp1));
//                    Log.d("EndlessLoop", String.format("%f", value));
//                    Log.d("EndlessLoop", String.format("%f", value - (int) value));
//                Log.v("EndlessLoop", String.format("%d", temp1 % 10));
                    break;
                }
            }

        if (precision == 0)
            return String.format(new Locale("ru","RU"), "%d", Math.round(value));
        else
            return String.format(new Locale("ru","RU"), "%." + precision + "f", value);

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
