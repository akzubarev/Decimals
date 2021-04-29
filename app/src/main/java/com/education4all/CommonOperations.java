package com.education4all;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.education4all.MathCoachAlg.StatisticMaker;
import com.education4all.MathCoachAlg.Tasks.Task;
import com.education4all.MathCoachAlg.Tours.Tour;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class CommonOperations {

    public static void FixDialog(AlertDialog dialog, Context context) {
//        Resources res = context.getResources();
//        TextView textView = dialog.findViewById(android.R.id.message);
//        textView.setTextSize(res.getDimension(R.dimen.dimen4) / res.getDisplayMetrics().density);
//        textView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
//
//        int colormain = res.getColor(R.color.main);
//        Button btn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//        if (btn != null)
//            btn.setTextColor(colormain);
//        btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        if (btn != null)
//            btn.setTextColor(colormain);
//        btn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
//        if (btn != null)
//            btn.setTextColor(colormain);
    }


    public static boolean requiresKostyl(String date) {
        String NEWVERSIONDATE = "05.04.2021";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(NEWVERSIONDATE));
            c2.setTime(sdf.parse(date));
            if (c2.before(c1))
                return true;

        } catch (
                ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void genereteFakeStatistics(Context context) {
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
            genereteFakeTour(stat, context);
    }


    public static void genereteFakeTour(String stat, Context context) {

        Random rnd = new Random();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -31);
        String[] lines = stat.split(" ");
        c.add(Calendar.DATE, Integer.parseInt(lines[0]));

        Tour tour = new Tour();
        tour.tourDateTime = c.getTimeInMillis();
        tour.tourTime = 0;

        for (int j = 1; j < lines.length; j++) {
            Task task = Task.makeTask();
            task.setTimeTaken(rnd.nextInt(100));
            tour.tourTime += task.getTimeTaken();

//                for (int k = 0; k < rnd.nextInt(4); k++) {
//                    int wronganswer = 0;
//                    do {
//                        wronganswer = rnd.nextInt(20);
//                    } while (String.valueOf(wronganswer).equals(task.answer));
//                    task.userAnswer += wronganswer + ":" + rnd.nextInt(20) + '|';
//                }

            if (Integer.parseInt(lines[j]) == 1) {
                task.setUserAnswer(task.getUserAnswer() + task.getAnswer() + ":" + rnd.nextInt(20) + '|');
                tour.rightTasks++;
            } else
                task.setUserAnswer(task.getUserAnswer() + "\u2026" + ":" + rnd.nextInt(20) + '|');
            tour.tourTasks.add(task);
        }

        tour.totalTasks = tour.tourTasks.size();
        StatisticMaker.saveTour(tour, context);
    }
}
