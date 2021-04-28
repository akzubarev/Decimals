package com.education4all.MathCoachAlg.Tours;

import com.education4all.MathCoachAlg.Tasks.SerializableTask;
import com.education4all.MathCoachAlg.Tasks.Task;

public class QueueItem {
    SerializableTask task;
    int delay = 1;
    int iteration = 1;
    boolean ready = false;
    boolean finished = false;

    public QueueItem() {
    }

    public QueueItem(Task task) {
        this.task = new SerializableTask(task);
    }

    public void reInit() {
        iteration = 1;
        delay = 1;
        ready = false;
        finished = false;
    }

    public Task getTask() {
        return task;
    }

    public int getDelay() {
        return delay;
    }

    public int getIteration() {
        return iteration;
    }

    public void decreaseDelay() {
        delay--;
        if (delay == 0)
            ready = true;
    }

    public Task show() {
        ready = false;
        iteration++;
        delay = iteration;
        if (iteration == 4)
            finished = true;

        return Task.makeTask(task);
    }

    public boolean validForTour(int operation, int complexity) {
        return task.getOperation() == operation && task.getComplexity() == complexity;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isFinished() {
        return finished;
    }
}

