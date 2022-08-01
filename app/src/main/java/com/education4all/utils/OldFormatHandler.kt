package com.education4all.utils;

import android.content.Context;

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

public class OldFormatHandler {
    static ArrayList<Tour> oldtours = new ArrayList<>();

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
                    if (requiresKostyl(date)) {
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

                            if (requiresKostyl(date))
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
