package com.education4all.MathCoachAlg.Tasks;

import java.util.Random;

public class IntegerTask extends Task {

    public IntegerTask(){
        expression = "2 + 2";
        answer = "4";
        userAnswer = "";
        rnd = new Random();
    }

    public IntegerTask(String line) {

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

    @Override
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



    // +
    private void addition(final int[][] allowedTasks) {
        int n = 0;
        int m = 0;
        complexity = complexityRandomizer(allowedTasks);
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
        if (rnd.nextBoolean()) {
            int k = n;
            n = m;
            m = k;
        }
        expression = n + operations[0] + m;
        answer = Integer.toString(n + m);
    }

    // -
    private void substraction(final int[][] allowedTasks) {
        int m = 0;
        int n = 0;
        complexity = complexityRandomizer(allowedTasks);
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
        if (rnd.nextBoolean()) {
            int k = n;
            n = m;
            m = k;
        }
        expression = (m + n) + operations[1] + m;
        answer = Integer.toString(n);
    }

    // *
    private void multiplication(final int[][] allowedTasks) {
        int n = 0;
        int m = 0;
        complexity = complexityRandomizer(allowedTasks);
        switch (complexity) {
            case 0:
                n = rnd.nextInt(8) + 2;
                m = rnd.nextInt(8) + 2;
                break;
            case 1:
                n = rnd.nextInt(8) + 2;
                m = rnd.nextInt(90) + 10;
                if (rnd.nextBoolean()) {
                    int k = n;
                    n = m;
                    m = k;
                }
                break;
            case 2:
                n = rnd.nextInt(8) + 2;
                m = rnd.nextInt(900) + 100;
                if (rnd.nextBoolean()) {
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
        expression = n + operations[2] + m;
        answer = Integer.toString(n * m);
    }

    // :
    private void division(final int[][] allowedTasks) {
        int m = 0;
        int n = 0;
        int k = 0;
        int l = 0;
        complexity = complexityRandomizer(allowedTasks);
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
//	                if (rnd.nextBoolean()) {
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
        expression = k * l + operations[3] + k;
        answer = Integer.toString(l);
    }
}
