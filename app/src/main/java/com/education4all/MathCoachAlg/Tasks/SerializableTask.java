package com.education4all.MathCoachAlg.Tasks;

public class SerializableTask extends Task {
    @Override
    public void generate() {
    }

    public SerializableTask() {
        //needed for serialization
    }

    public SerializableTask(Task t) {
        expression = t.expression;
        operation = t.operation;
        complexity = t.complexity;
        answer = t.answer;
        userAnswer = t.userAnswer;
        userAnswerTime = t.userAnswerTime;
    }
}
