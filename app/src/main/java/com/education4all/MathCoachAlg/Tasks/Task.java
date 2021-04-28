package com.education4all.MathCoachAlg.Tasks;

import java.util.ArrayList;
import java.util.Random;


public abstract class Task {
    String expression = "";
    int operation = 0;
    int complexity = 0;
    String answer = "";
    String userAnswer = "";
    long taskTime = 0;
    long timeTaken = 0;
    Random rnd = new Random();
    static final String[] operations = {"\u2006+\u2006", "\u2006−\u2006", "\u2006\u22C5\u2006", "\u2006:\u2006"};
    static String type = "decimals";
    static int[][] allowedTasks;

    public String getExpression() {
        return expression;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public String getAnswer() {
        return answer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public long getTaskTime() {
        return taskTime;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public static String[] getOperations() {
        return operations;
    }

    public int getComplexity() {
        return complexity;
    }

    public static String getType() {
        return type;
    }

    public static int[][] getAllowedTasks() {
        return allowedTasks;
    }

    public static void setAllowedTasks(int[][] allowedTasks) {
        Task.allowedTasks = allowedTasks;
    }

    public static void setType(String type) {
        Task.type = type;
    }

    public Task() {
        expression = "2 + 2";
        answer = "4";
        userAnswer = "";
    }

    public static Task makeTask(Task t) {
        Task newTask = makeTask();
        newTask.expression = t.expression;
        newTask.operation = t.operation;
        newTask.complexity = t.complexity;
        newTask.answer = t.answer;
        newTask.userAnswer = "";
        return newTask;
    }

    public static Task makeTask() {
        Task task;
        switch (type) {
            case "integers":
            default:
                task = new IntegerTask();
                break;
            case "decimals":
                task = new DecimalTask();
                break;
            case "fractions":
                task = new FractionTask();
                break;
        }
        task.generate();
        return task;
    }

    public static Task makeTask(String line) {
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

    public static String DepictTask(String line) {
        Task task = makeTask(line);
//		return l_task.expression + " = " + l_task.userAnswer + "(" + l_task.answer + ") " + l_task.timeTaken + " сек. ";
        return task.expression + " = " + task.userAnswer + " " + task.timeTaken + " сек. ";
    }

    public static ArrayList<String> DepictTaskExtended(String line, String type, ArrayList<String> answers) {
        Task task = makeTask(line);
        ArrayList<String> res = new ArrayList<>();
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

    public abstract void generate();

    public static boolean areTasks(int[][] p_allowedTasks) {
        for (int[] p_allowedTask : p_allowedTasks)
            if (p_allowedTask.length > 0)
                return true;
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

    ArrayList<Integer> availableOperations(int[][] allowedTasks) {
        ArrayList<Integer> out = new ArrayList<>();
        for (int i = 0; i < allowedTasks.length; i++)
            if (allowedTasks[i].length > 0)
                out.add(i);
        return out;
    }

    int operationRandomizer(final int[][] allowedTasks) {
        ArrayList<Integer> choices = availableOperations(allowedTasks);
        return choices.get(rnd.nextInt(choices.size()));
    }

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
