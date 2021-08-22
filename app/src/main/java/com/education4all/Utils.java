package com.education4all;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.education4all.mathCoachAlg.DataReader;
import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tasks.Task;
import com.education4all.mathCoachAlg.tours.Tour;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class CommonOperations {

    static ArrayList<Tour> oldtours = new ArrayList<>();

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


    public static void save(Context context) {
        oldtours = StatisticMaker.loadToursOLD(context);
        String json = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(oldtours);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DataReader.SaveStat(json, context);
    }

    public static void load(Context context) {
        if (oldtours.size() > 0)
            for (Tour tour : oldtours)
                StatisticMaker.saveTourOLD(tour, context);
        else {
            try {
                String json = DataReader.GetStat(context);
                ObjectMapper objectMapper = new ObjectMapper();
                oldtours = objectMapper.readValue(json, new TypeReference<ArrayList<Tour>>() {
                });
                for (Tour tour : oldtours)
                    StatisticMaker.saveTourOLD(tour, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean requiresKostyl(String date) {
        String NEWVERSIONDATE = "05.04.2021";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar newversiondate = Calendar.getInstance();
        Calendar currentdate = Calendar.getInstance();
        try {
            newversiondate.setTime(sdf.parse(NEWVERSIONDATE));
            currentdate.setTime(sdf.parse(date));
            if (currentdate.before(newversiondate))
                return true;

        } catch (
                ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

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

            if (Integer.parseInt(lines[j]) == 1)
                if (old)
                    task.makeUserAnswer(task.getAnswer() + ":" + rnd.nextInt(20) + '|', "");
                else
                    task.makeUserAnswer(task.getAnswer(), String.valueOf(rnd.nextInt(20)));
            else if (old)
                task.makeUserAnswer("\u2026" + ":" + rnd.nextInt(20) + '|', "");
            else
                task.makeUserAnswer("\u2026", String.valueOf(rnd.nextInt(20)));

            tour.addTaskOld(task, Integer.parseInt(lines[j]) == 1);
        }
        return tour;
    }

    public static void updateStatistics(Context context) {
        if (StatisticMaker.getTourCount(context) > 0) {
            ArrayList<Tour> update = new ArrayList<>();
            for (int tourNumber = 0; tourNumber < StatisticMaker.getTourCount(context); tourNumber++) {

                String tourInfoStr = StatisticMaker.getTourInfoOLD(context, tourNumber);
                if (!tourInfoStr.isEmpty()) {
                    String txt = Tour.DepictTour(tourInfoStr);

                    int divider = txt.indexOf("Решено");

                    String datetime = txt.substring(1, divider - 1);
                    String date = datetime.substring(0, 10);

                    Tour oldtour = StatisticMaker.loadTourOld(context, tourNumber);
                    Tour newTour = new Tour();
                    newTour.copy(oldtour);
                    if (CommonOperations.requiresKostyl(date)) {
                        newTour.getTourTasks().clear();
                        ArrayList<String> deTour = oldtour.serializeOLD();
                        int jump = 0;
                        for (int i = 1; i < deTour.size() - 1; ++i) {

                            ArrayList<String> answers = new ArrayList<>();
                            Task currentTask = Task.makeTask(deTour.get(i));
                            ArrayList<String> TaskDepiction = Task.DepictTaskExtended(deTour.get(i), answers);
                            Task newTask = Task.makeTask();
                            newTask.copy(currentTask);
                            newTask.update();
                            newTour.addTask(newTask);

                            if (CommonOperations.requiresKostyl(date))
                                for (int j = 0; j < TaskDepiction.size(); ++j) {
                                    boolean answerIsCorrect = answers.get(j).equals(currentTask.getAnswer());
                                    boolean taskWasSkipped = answers.get(j).equals("\u2026");
                                    if (answerIsCorrect || taskWasSkipped) {
                                        i += jump;
                                        jump = 0;
                                    } else {
                                        ++jump;
                                    }
                                    if (i + jump == deTour.size() - 2) {
                                        i += jump;
                                    }
                                }
                        }
                    } else
                        for (Task task : newTour.getTourTasks())
                            task.update();
                    update.add(newTour);
                } else
                    update.add(StatisticMaker.loadTour(context, tourNumber));
            }

            StatisticMaker.removeStatistics(context);
            for (Tour tour : update)
                StatisticMaker.saveTour(tour, context);
        }
    }
}
