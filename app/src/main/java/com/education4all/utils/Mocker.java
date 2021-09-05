package com.education4all.utils;

import android.content.Context;

import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tasks.Task;
import com.education4all.mathCoachAlg.tours.Tour;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Mocker {


    public static void genereteFakeStatistics(Context context, boolean old) {
        String[] stats = new String[]{
                "1 0 0 1 1 1 1 1 1 1 1 0",
                "2 1 1 1 1 1 1 1 1 1 0 1",
                "3 1 1 1 1 1 1 1 0 1 1 1",
                "4 1 0 1 1 1 1 1 1 0 1 1",
                "5 1 0 1 1 1 1 0 1 1 1 1",
                "6 1 1 1 1 1 1 1 0 1 1 1",
                "7 0 1 1 1 1 1 1 1 1 0 1",
                "8 1 0 1 1 0 1 1 1 1 1 0",
                "9 1 1 1 1 1 1 1 1 1 1 0",
                "10 1 1 1 1 1 0 1 1 1 0 0"
        };

        for (String stat : stats)
            generateFakeTour(stat, context, old);
    }

    public static void generateFakeTour(String stat, Context context, boolean old) {
        if (old)
            StatisticMaker.saveTourOLD(genTour(stat, true), context);
        else
            StatisticMaker.saveTour(genTour(stat, false), context);
    }

    private static Tour genTour(String stat, boolean old) {

        Random rnd = new Random();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -31);
        String[] lines = stat.split(" ");
        c.add(Calendar.DATE, Integer.parseInt(lines[0]));

        Tour tour = new Tour();
        tour.setTourDateTime(c.getTimeInMillis());
        tour.setTourTime(0);

        for (int j = 1; j < lines.length; j++) {
            Task task = Task.makeTask();
            task.setTimeTaken(rnd.nextInt(100));
            tour.setTourTime(tour.getTourTime() + task.getTimeTaken());

            boolean correct = Integer.parseInt(lines[j]) == 1;
            if (old)
                task.makeUserAnswer(correct ? task.getAnswer() : "…" + ":" + rnd.nextInt(20) + '|', "");
            else
                task.makeUserAnswer(correct ? task.getAnswer() : "…", String.valueOf(rnd.nextInt(20)));
            tour.addTaskOld(task, correct);
        }
        return tour;
    }

}
