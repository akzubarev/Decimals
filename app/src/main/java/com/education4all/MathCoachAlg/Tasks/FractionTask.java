package com.education4all.mathCoachAlg.tasks;

import android.util.Log;
import android.util.Pair;

import com.education4all.utils.Utils;

import java.util.ArrayList;

public class FractionTask extends Task {
    public FractionTask() {
        expression = Fraction.makeValue("3", "1", "2") + operations[0] + Fraction.makeValue("2", "1", "3");
        answer = Fraction.makeValue("5", "5", "6");
        userAnswer = "";
    }

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

    private ArrayList<Integer> factorize(int a) {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 2; ((double) i) <= Math.sqrt((double) a); i++) {
            while (a % i == 0) {
                res.add(i);
                a /= i;
            }
        }
        if (a != 1)
            res.add(a);
        return res;
    }

    private boolean hasOtherParts(int a, int b) {
        ArrayList<Integer> aParts = factorize(a);
        ArrayList<Integer> bParts = factorize(b);
        for (Integer aPart : aParts)
            if (!bParts.contains(aPart))
                return true;
        return false;
    }

    private boolean isPrime(int num) {
        if (num < 2) {
            return false;
        }
        double s = Math.sqrt((double) num);
        for (int i = 2; ((double) i) <= s; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    private Pair<Integer, Integer> generateBottoms(int complexity, boolean simplifiable) {
        int secondBottom = -1, firstBottom = -1;
        int b = -1, d = -1, e = -1;

        switch (complexity) {
            case 0:
                ArrayList<Pair<Integer, Integer>> probabilities = new ArrayList<>();

                probabilities.add(new Pair(0, 95));
                probabilities.add(new Pair(1, 95));
                probabilities.add(new Pair(2, 95));
                if (!simplifiable)
                    probabilities.add(new Pair(3, 15));

                do {
                    switch (Utils.randomly_select(probabilities)) {
                        case 0:
                            firstBottom = randomInclusive(3, 10);
                            break;
                        case 1:
                            firstBottom = randomInclusive(11, 20);
                            break;
                        case 2:
                            firstBottom = randomInclusive(21, 100);
                            break;
                        case 3:
                            secondBottom = 1;
                            firstBottom = randomInclusive(3, 100);
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                } while (simplifiable && isPrime(firstBottom));
                break;
            case 1:
                if (rnd.nextBoolean()) {
                    firstBottom = randomInclusive(2, 9);
                    do {
                        secondBottom = randomInclusive(11, 100 / firstBottom);
                    } while (gcd(firstBottom, secondBottom) != 1);
                } else {
                    firstBottom = randomInclusive(2, 10);
                    do
                        secondBottom = randomInclusive(2, Math.min(100 / firstBottom, 10));
                    while (gcd(firstBottom, secondBottom) != 1);
                }
                break;
            case 2:
                if (simplifiable) {
                    do {
                        b = randomInclusive(3, 10);
                        d = randomInclusive(3, 100 / b);
                    } while (!hasOtherParts(b, d));
                } else {
                    do {
                        b = randomInclusive(2, 10);
                        d = randomInclusive(2, 100 / b);
                    } while (b % 2 == 0);
                }
                secondBottom = b * d;
                firstBottom = b;
                break;
            case 3:
                if (simplifiable) {
                    b = randomInclusive(2, 10);
                    do
                        d = randomInclusive(2, 10);
                    while (b % d == 0 && d % b == 0);

                    do
                        e = randomInclusive(2, 10);
                    while (gcd(b, e) != 1 || gcd(d, e) != 1);
                } else {
                    b = randomInclusive(2, 10);
                    do
                        d = randomInclusive(2, 10);
                    while (b % d == 0 || d % b == 0 || (b % 2 == 0 && d % 2 == 0));
                    do
                        e = randomInclusive(2, 10);
                    while (e % 2 == 0);
                }

                firstBottom = b * e;
                secondBottom = d * e;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return new Pair<>(firstBottom, secondBottom);
    }

    private int secondTop(String sym, int secondBottom,
                          int firstTop, int firstBottom,
                          boolean canbedividable, boolean simplifiable) {

        int leftborder = 1, rightborder = secondBottom - 1;
        int secondTop = 0;
        double firstpercent = ((double) firstTop) / firstBottom;
        if (sym.equals("+")) {
            double border = ((1 - firstpercent) * secondBottom);
            if (firstpercent < 0.5)
                rightborder = (int) border;
            else
                leftborder = (int) Math.ceil(border);
        } else if (sym.equals("-")) {
            double border = firstpercent * secondBottom;
            if (firstpercent < 0.5)
                leftborder = (int) Math.ceil(border);
            else
                rightborder = (int) border;
        }

        if (leftborder < 1)
            leftborder = 1;
        if (rightborder > secondBottom - 1)
            rightborder = secondBottom - 1;

        if (rightborder < 1)
            rightborder = 1;
        if (leftborder > secondBottom - 1)
            leftborder = secondBottom - 1;

        Fraction left = new Fraction(0, firstTop, firstBottom).simplify();
        boolean resultIsSimpl = false;
        int debugcount = 0;
        do {
            debugcount++;
            if (debugcount > 10) {
                Log.d("generatingexception",
                        String.format("Simpl: %b, Left: %s, RightBottom: %d",
                                simplifiable, left.toString(), secondBottom)
                );
                return -1;
            }
            secondTop = topFromBottom(secondBottom, leftborder, rightborder, canbedividable);
            Fraction right = new Fraction(0, secondTop, secondBottom).simplify();
            Fraction result = left.plus(right);

            int commonBottom = left.get_bottom() * right.get_bottom() /
                    gcd(left.get_bottom(), right.get_bottom());
            resultIsSimpl = commonBottom != result.get_bottom();

        } while (resultIsSimpl != simplifiable);
        return secondTop;
    }

    private int integer_part() {
        switch (randomInclusive(1, 3)) {
            case 1:
                return 0;
            case 2:
                return randomInclusive(1, 10);
            case 3:
                return randomInclusive(11, 100);
            default:
                throw new IllegalArgumentException();
        }
    }

    private int integer_part(Fraction second, int firstTop, int firstBottom) {
        int random;
        int leftborder = second.more(new Fraction(second.get_integer(), firstTop, firstBottom)) ? second.get_integer() + 1 : second.get_integer();
        if (leftborder < 1) {
            random = randomInclusive(1, 3);
        } else if (leftborder < 11) {
            random = randomInclusive(2, 3);
        } else {
            random = 3;
        }
        switch (random) {
            case 1:
                return leftborder;
            case 2:
                return randomInclusive(leftborder, 10);
            case 3:
                return randomInclusive(leftborder, 100);
            default:
                throw new IllegalArgumentException();
        }
    }

    private int topFromBottom(int b, boolean canBeDividable) {
        int debugcount = 0;
        int a = randomInclusive(1, b - 1);
        if (canBeDividable) {
            return a;
        }
        while (gcd(a, b) != 1) {
            a = randomInclusive(1, b - 1);
            debugcount++;
            if (debugcount == 100) {
                Log.d("generatingexception", "aFromB");
                return -1;
            }
        }
        return a;
    }

    private int topFromBottom(int b, int leftborder, int rightborder, boolean canBeDividable) {
        int debugcount = 0;
        int a = randomInclusive(leftborder, rightborder);
        if (canBeDividable) {
            return a;
        }
        while (gcd(a, b) != 1) {
            a = randomInclusive(leftborder, rightborder);
            debugcount++;
            if (debugcount == 100) {
                Log.d("generatingexception", "aFromB");
                return -1;
            }
        }
        return a;
    }

    private int gcd(int p, int q) {
        if (q == 0) {
            return p;
        }
        return gcd(q, p % q);
    }

    private void addition(int[][] allowedTasks) {
        int firstInt, firstTop, firstBottom;
        int secondInt, secondTop, secondBottom;

        boolean canbedividable, simplifiable;
        boolean oneZero;

        Fraction left, right;

        complexity = complexityRandomizer(allowedTasks);

        simplifiable = rnd.nextBoolean();
        if (complexity == 1) {
            simplifiable = false;
        }

        Pair<Integer, Integer> bottoms = generateBottoms(complexity, simplifiable);
        firstBottom = bottoms.first;

        if (complexity == 0) {
            secondBottom = firstBottom;
            oneZero = bottoms.second == 1;
            canbedividable = true;
        } else {
            oneZero = false;
            canbedividable = false;
            secondBottom = bottoms.second;
        }

        firstInt = integer_part();
        firstTop = topFromBottom(firstBottom, canbedividable);
        secondInt = integer_part();


        if (oneZero) {
            secondTop = 0;
        } else {
            int debugcount = 0;
            do {
                debugcount += 1;
                if (debugcount > 10) {
                    expression = new Fraction(firstInt, firstTop, firstBottom).simplify() + "";
                    answer = "error";
                    Log.e("generatingexception", "to many iterations");
                    return;
                }
                firstTop = topFromBottom(firstBottom, canbedividable);
                secondTop = secondTop("+", secondBottom, firstTop, firstBottom, canbedividable, simplifiable);
            } while (secondTop == -1);
        }
        if (!canbedividable || rnd.nextBoolean())
            left = new Fraction(firstInt, firstTop, firstBottom).simplify();
        else
            left = new Fraction(firstInt, firstTop, firstBottom).remove_integer_part();

        if (!canbedividable || rnd.nextBoolean())
            right = new Fraction(secondInt, secondTop, secondBottom).simplify();
        else
            right = new Fraction(secondInt, secondTop, secondBottom).remove_integer_part();

        if (rnd.nextBoolean()) {
            Fraction temp = left;
            left = right;
            right = temp;
        }

        expression = left + operations[0] + right;
        answer = left.plus(right).toString();
    }

    private void substraction(int[][] allowedTasks) {
        int secondInt, secondTop, secondBottom;
        int firstInt, firstTop, firstBottom;
        boolean canbedividable, simplifiable;
        boolean oneZero;

        Fraction left, right;

        complexity = complexityRandomizer(allowedTasks);
        simplifiable = rnd.nextBoolean();
        if (complexity == 1) {
            simplifiable = false;
        }
        Pair<Integer, Integer> bottoms = generateBottoms(complexity, simplifiable);

        firstBottom = bottoms.first;
        if (complexity == 0) {
            secondBottom = firstBottom;
            oneZero = bottoms.second == 1;
            if (oneZero) {
                simplifiable = false;
            }
            canbedividable = true;
        } else {
            oneZero = false;
            canbedividable = false;
            secondBottom = bottoms.second;
        }
        firstTop = topFromBottom(firstBottom, canbedividable);
        secondInt = integer_part();

        if (oneZero) {
            secondTop = 0;
        } else {
            int debugcount = 0;
            do {
                debugcount += 1;
                if (debugcount > 10) {
                    expression = new Fraction(secondInt, firstTop, firstBottom).simplify() + "";
                    answer = "error";
                    Log.e("generatingexception", "to many iterations");
                    return;
                }
                firstTop = topFromBottom(firstBottom, canbedividable);
                secondTop = secondTop("-", secondBottom, firstTop, firstBottom, canbedividable, simplifiable);
            } while (secondTop == -1);
        }

        if (!canbedividable || !rnd.nextBoolean())
            right = new Fraction(secondInt, secondTop, secondBottom).simplify();
        else
            right = new Fraction(secondInt, secondTop, secondBottom).remove_integer_part();


        firstInt = integer_part(right, firstTop, firstBottom);

        if (!canbedividable || !rnd.nextBoolean()) {
            left = new Fraction(firstInt, firstTop, firstBottom).simplify();
        } else {
            left = new Fraction(firstInt, firstTop, firstBottom).remove_integer_part();
        }

        expression = left + operations[1] + right;
        answer = left.minus(right).toString();
    }

    private void multiplication(int[][] allowedTasks) {
        Fraction left;
        Fraction right;
        complexity = complexityRandomizer(allowedTasks);
        switch (complexity) {
            case 0:
                int b = randomInclusive(2, 10);
                int d = randomInclusive(2, 10);
                int a = topFromBottom(b, false);
                int c = topFromBottom(d, false);
                left = new Fraction(0, a, b).simplify();
                right = new Fraction(0, c, d).simplify();
                break;
            default:
                left = Fraction.null_element();
                right = Fraction.null_element();
                break;
        }
        expression = left + operations[2] + right;
        answer = left.mult(right).toString();
    }

    private void division(int[][] allowedTasks) {
        Fraction left;
        Fraction right;
        complexity = complexityRandomizer(allowedTasks);
        switch (complexity) {
            case 0:
                int b = randomInclusive(2, 10);
                int d = randomInclusive(2, 10);
                int a = topFromBottom(b, false);
                int c = topFromBottom(d, false);
                left = new Fraction(0, a, b).simplify();
                right = new Fraction(0, c, d).simplify();
                break;
            default:
                left = Fraction.null_element();
                right = Fraction.null_element();
                break;
        }
        expression = left + operations[3] + right;
        answer = left.div(right).toString();
    }
}
