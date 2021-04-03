package com.education4all.MathCoachAlg.Tasks;

import java.util.ArrayList;
import java.util.Random;


public abstract class Task {
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

    public static Task makeTask(String type) {
        Task task;
        switch (type) {
            case "integer":
            default:
                task = new IntegerTask();
                break;
            case "decimal":
                task = new DecimalTask();
                break;
            case "fractions":
                task = new FractionTask();
                break;
        }
        return task;
    }

    public static Task makeTask(String line, String type) {
        Task task;
        switch (type) {
            case "integer":
            default:
                task = new IntegerTask(line);
                break;
            case "decimal":
                task = new DecimalTask(line);
                break;
            case "fractions":
                task = new FractionTask(line);
                break;
        }
        return task;
    }

    public static String DepictTask(String line, String type) {
        Task task = makeTask(line, type);
//		return l_task.expression + " = " + l_task.userAnswer + "(" + l_task.answer + ") " + l_task.timeTaken + " сек. ";
        return task.expression + " = " + task.userAnswer + " " + task.timeTaken + " сек. ";
    }

    public static ArrayList<String> DepictTaskExtended(String line, String type, ArrayList<String> answers) {
        Task task = makeTask(line, type);
        ArrayList<String> res = new ArrayList<String>();
        String firstPart = task.expression + " = ";
        String partToBeParsed = task.userAnswer;
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

    public abstract void generate(final int[][] allowedTasks);

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

    // ����� ������ ��������
    int operationRandomizer(final int[][] allowedTasks) {
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
    int complexityRandomizer(final int[][] allowedTasks) {
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

}
