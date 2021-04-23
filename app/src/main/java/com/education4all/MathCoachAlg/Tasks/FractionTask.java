package com.education4all.MathCoachAlg.Tasks;

import java.util.Random;

public class FractionTask extends Task {


    public FractionTask() {
        expression = Fraction.makeValue("3", "1", "2") + operations[0]
                + Fraction.makeValue("2", "1", "3");
        answer = Fraction.makeValue("5", "5", "6");

        userAnswer = "";
        rnd = new Random();
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
        int A, B, C, D, E, F;
        A = rnd.nextInt(10);

        C = 2 + rnd.nextInt(8);
        B = 1 + rnd.nextInt(C - 1);

        D = rnd.nextInt(10);

        F = 2 + rnd.nextInt(8);
        E = 1 + rnd.nextInt(F - 1);

        Fraction left = new Fraction(A, B, C).simplify();
        Fraction right = new Fraction(D, E, F).simplify();

        expression = left + operations[0] + right;
        answer = left.plus(right).toString();
    }


    // -
    private void substraction(final int[][] allowedTasks) {
        int A, B, C, D, E, F;
       // A = rnd.nextInt(10);

        C = 2 + rnd.nextInt(8);
        B = 1 + rnd.nextInt(C - 1);

        D = rnd.nextInt(10);

        F = 2 + rnd.nextInt(8);
        E = 1 + rnd.nextInt(F - 1);

        A = D + rnd.nextInt(11 - D);

        Fraction left = new Fraction(A, B, C).simplify();
        Fraction right = new Fraction(D, E, F).simplify();

        if (D == A)
            while (right.more(left)) {
                C = 2 + rnd.nextInt(8);
                B = 1 + rnd.nextInt(C - 1);
                left = new Fraction(A, B, C).simplify();

                F = 2 + rnd.nextInt(8);
                E = 1 + rnd.nextInt(F - 1);
                right = new Fraction(D, E, F).simplify();
            }

        expression = left + operations[1] + right;
        answer = left.minus(right).toString();
    }

    // *
    private void multiplication(final int[][] allowedTasks) {
        int A, B, C, D, E, F;
        A = rnd.nextInt(10);

        C = 2 + rnd.nextInt(8);
        B = 1 + rnd.nextInt(C - 1);

        D = rnd.nextInt(10);

        F = 2 + rnd.nextInt(8);
        E = 1 + rnd.nextInt(F - 1);

        Fraction left = new Fraction(A, B, C).simplify();
        Fraction right = new Fraction(D, E, F).simplify();

        expression = left + operations[2] + right;
        answer = left.mult(right).toString();
    }

    // :
    private void division(final int[][] allowedTasks) {
        int A, B, C, D, E, F;
        A = rnd.nextInt(10);

        C = 2 + rnd.nextInt(8);
        B = 1 + rnd.nextInt(C - 1);

        D = rnd.nextInt(10);

        F = 2 + rnd.nextInt(8);
        E = 1 + rnd.nextInt(F - 1);

        Fraction left = new Fraction(A, B, C).simplify();
        Fraction right = new Fraction(D, E, F).simplify();

        expression = left + operations[3] + right;
        answer = left.div(right).toString();
    }

}
