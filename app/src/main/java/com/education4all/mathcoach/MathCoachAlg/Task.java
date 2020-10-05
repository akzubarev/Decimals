package com.education4all.mathcoach.MathCoachAlg;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
    public final String[] operations = {"\u2006+\u2006", "\u2006−\u2006", "\u2006\u22C5\u2006", "\u2006:\u2006"};

    public Task() {
        expression = "2 + 2";
        answer = "4";
        userAnswer = new String();
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
            partToBeParsed = partToBeParsed.substring(commaIndex + 1, partToBeParsed.length());
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
        return expression + ";" + Integer.toString(operation) + ";" + Integer.toString(complexity) + ";" +
                answer + ";" + userAnswer + ";" + Long.toString(taskTime) + ";" +
                Long.toString(timeTaken) + ";";
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

    int generateRandomInclusive(int left, int right) {
        return rnd.nextInt(right - left + 1) + left;

    }

    String withPrecision(double value) {
        int precision = 0;
        double temp = value;
        while (temp - (int) temp > 0.00001) {
            if (precision > 50 || temp - (int) temp >= 1) {
                //Log.v("Endless loop", String.format("%f", value));
                int temp1 = (int)temp+1;
                while (temp1%10==0)
                {
                    temp1/=10;
                    precision--;
                }
                break;
            }
            temp *= 10f;
            precision++;

        }
        if (precision == 0)
            return String.format("%d", (int) value);
        else
            return String.format("%." + precision + "f", value);

    }

    // +
    private void addition(final int[][] allowedTasks) {
        int n = 0;
        int m = 0;
        complexity = complexityRandomizer(allowedTasks);
        ;
        switch (complexity) {
            case 0:
                switch (generateRandomInclusive(0, 2)) {
                    case 0:
                        n = generateRandomInclusive(1, 9);
                        break;
                    case 1:
                        n = generateRandomInclusive(10, 99);
                        break;
                    case 2:
                    default:
                        n = generateRandomInclusive(100, 999);
                        break;
                }
                m = generateRandomInclusive(1, 9);
                break;
            case 1:
                switch (generateRandomInclusive(0, 1)) {
                    case 0:
                        n = generateRandomInclusive(10, 99);
                        break;
                    case 1:
                    default:
                        n = generateRandomInclusive(100, 999);
                        break;
                }
                m = generateRandomInclusive(10, 99);
                break;
            case 2:
                if (rnd.nextBoolean())
                    switch (generateRandomInclusive(0, 3)) {
                        case 0:
                            n = generateRandomInclusive(1, 9) * 10;
                            m = generateRandomInclusive(10, 99);
                            break;
                        case 1:
                            n = generateRandomInclusive(1, 9) * 10;
                            m = generateRandomInclusive(100, 999);
                            break;
                        case 2:
                            n = generateRandomInclusive(10, 99) * 10;
                            m = generateRandomInclusive(10, 99);
                            break;
                        case 3:
                        default:
                            n = generateRandomInclusive(100, 999) * 10;
                            m = generateRandomInclusive(10, 99);
                            break;
                    }
                else
                    switch (generateRandomInclusive(0, 2)) {
                        case 0:
                            n = generateRandomInclusive(1, 9) * 100;
                            m = generateRandomInclusive(100, 999);
                            break;
                        case 1:
                            n = generateRandomInclusive(10, 99) * 100;
                            m = generateRandomInclusive(100, 999);
                            break;
                        case 2:
                        default:
                            n = generateRandomInclusive(100, 999) * 100;
                            m = generateRandomInclusive(100, 999);
                            break;
                    }
                break;
            case 3:
                if (rnd.nextBoolean()) {
                    n = generateRandomInclusive(10, 99) * 10;
                    m = generateRandomInclusive(100, 999);
                } else {
                    n = generateRandomInclusive(100, 999) * 10;
                    m = generateRandomInclusive(100, 999);
                }
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

        int pow = generateRandomInclusive(1, 3);
        double div = Math.pow(10, pow);
        double nn = n / div, mm = m / div;

        expression = withPrecision(nn) + operations[0] + withPrecision(mm);
        answer = withPrecision(nn + mm);
    }

    // -
    private void substraction(final int[][] allowedTasks) {
        int m = 0;
        int n = 0;
        complexity = complexityRandomizer(allowedTasks);
        ;
        switch (complexity) {
            case 0:
                n = rnd.nextInt(9 + 1); //От 0 до 9
                m = rnd.nextInt(9 + 1); //От 0 до 9
                break;
            case 1:
                n = rnd.nextInt(99 - 10 + 1) + 10; //От 10 до 99
                m = rnd.nextInt(99 + 1); //От 0 до 99
                break;
            case 2:
                n = rnd.nextInt(999 - 100 + 1) + 100; //От 100 до 999
                m = rnd.nextInt(999 + 1);
                break;
            case 3:
                n = rnd.nextInt(9999 - 1000 + 1) + 1000; //От 1000 до 9999
                m = rnd.nextInt(9999 + 1); //От 0 до 9999
                break;
            default:
                ////DatasaveErrorLog("No valid complexity for addition, yet called");
                break;
        }
        if (rnd.nextInt(1) == 0) {
            int k = n;
            n = m;
            m = k;
        }
        expression = Integer.toString(m + n) + operations[1] + Integer.toString(m);
        answer = Integer.toString(n);
    }

    // *
    private void multiplication(final int[][] allowedTasks) {
        int n = 0;
        int m = 0;
        complexity = complexityRandomizer(allowedTasks);
        ;
        switch (complexity) {
            case 0:
                n = rnd.nextInt(8) + 2;
                m = rnd.nextInt(8) + 2;
                break;
            case 1:
                n = rnd.nextInt(8) + 2;
                m = rnd.nextInt(90) + 10;
                if (rnd.nextInt(2) == 1) {
                    int k = n;
                    n = m;
                    m = k;
                }
                break;
            case 2:
                n = rnd.nextInt(8) + 2;
                m = rnd.nextInt(900) + 100;
                if (rnd.nextInt(2) == 1) {
                    int k = n;
                    n = m;
                    m = k;
                }
                break;
            case 3:
                n = rnd.nextInt(90) + 10;
                m = rnd.nextInt(90) + 10;
                break;
            default:
                //DatasaveErrorLog("No valid complexity for multiplication, yet called");
                break;
        }
        expression = Integer.toString(n) + operations[2] + Integer.toString(m);
        answer = Integer.toString(n * m);
    }

    // :
    private void division(final int[][] allowedTasks) {
        int m = 0;
        int n = 0;
        int k = 0;
        int l = 0;
        complexity = complexityRandomizer(allowedTasks);
        ;
        switch (complexity) {
            case 0:
                n = rnd.nextInt(9 - 1 + 1) + 1;
                m = rnd.nextInt(9 + 1);
                k = n;
                l = m;
                break;
            case 1:
                n = rnd.nextInt(10 - 1 + 1) + 1;
                m = rnd.nextInt(99 - 10 + 1) + 10;
//	                if (rnd.nextInt(2) == 1) {
//	                    int p = n;
//	                    n = m;
//	                    m = p;
//	                }
                k = n;
                l = m;
                break;
            case 4:
                //На самом деле расширение второго уровня
                n = rnd.nextInt(10 - 1 + 1) + 1;
                m = rnd.nextInt(99 - 10 + 1) + 10;
                k = m;
                l = n;
                break;
            case 2:
                n = rnd.nextInt(10 - 1 + 1) + 1;
                m = rnd.nextInt(999 - 100 + 1) + 100;
                k = n;
                l = m;
                break;
            case 3:
                n = rnd.nextInt(99 - 10 + 1) + 10;
                m = rnd.nextInt(99 - 10 + 1) + 10;
                k = n;
                l = m;
                break;

            default:
                //DatasaveErrorLog("No valid complexity for division, yet called");
                k = 1;
                l = 1;
                break;
        }
        expression = Integer.toString(k * l) + operations[3] + Integer.toString(k);
        answer = Integer.toString(l);
    }
}
