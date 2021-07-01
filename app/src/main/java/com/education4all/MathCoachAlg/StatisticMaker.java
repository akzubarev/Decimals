package com.education4all.MathCoachAlg;

import android.content.Context;
import android.content.SharedPreferences;

import com.education4all.MathCoachAlg.Tours.Tour;

import java.util.ArrayList;


public class StatisticMaker {

    public static final String STATISTICS = "Statistics";
    public static final String TOURS = "tours";


    public static void saveTour(Tour tour, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);

        String tourCountStr = prefs.getString(TOURS, "0");
        int tourCount = Integer.parseInt(tourCountStr);
        saveTour_by_number(tour, context, tourCount);
        editor.putString(TOURS, Integer.toString(tourCount + 1));
        editor.apply();
    }

    public static void saveTourOLD(Tour tour, Context context) {
        SharedPreferences.Editor l_editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        String tourCountStr = prefs.getString(TOURS, "0");
        int tourCount = Integer.parseInt(tourCountStr);
        l_editor.putString(TOURS + "_" + tourCountStr, Integer.toString(tour.getTotalTasks()));
        ArrayList<String> serializedTour = tour.serializeOLD();
        for (int taskNumber = 0; taskNumber < tour.getTotalTasks() + 2; ++taskNumber) {
            l_editor.putString(TOURS + "_" + tourCountStr + "_" + taskNumber, serializedTour.get(taskNumber));
        }
        l_editor.putString(TOURS, Integer.toString(tourCount + 1));
        l_editor.apply();
    }

    public static ArrayList<Tour> loadToursOLD(Context context) {
        ArrayList<Tour> resultTours = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        int toursCount = Integer.parseInt(prefs.getString(TOURS, "0"));

        for (int tourNumber = 0; tourNumber < toursCount; ++tourNumber)
            resultTours.add(loadTourOld(context, tourNumber));

        return resultTours;
    }

    public static Tour loadTour(Context context, int tourNumber) {
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        String currentTour = prefs.getString(TOURS + "_" + tourNumber, "");
        return Tour.deSerialize(currentTour);
    }

    public static Tour loadTourOld(Context context, int tourNumber) {
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        int taskCount = Integer.parseInt(prefs.getString(TOURS + "_" + tourNumber, "0"));
        ArrayList<String> currentTour = new ArrayList<>();
        for (int taskNumber = 0; taskNumber < taskCount + 2; ++taskNumber) {
            currentTour.add(prefs.getString(TOURS + "_" + tourNumber + "_" + taskNumber, "0"));
        }
        if (currentTour.size() > 0) {
            return new Tour(currentTour);
        } else {
            return null;
        }
    }

    public static int getTourCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        return Integer.parseInt(prefs.getString(TOURS, "0"));
    }

    public static String getTourInfoOLD(Context context, int tourNumber) {
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        return prefs.getString(TOURS + "_" + tourNumber + "_" + "0", "");
    }

    public static void removeStatistics(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public static void removeTour(Context context, int tourNumber) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        editor.remove(TOURS + "_" + tourNumber);
        int toursCount = Integer.parseInt(prefs.getString(TOURS, "0"));
        editor.putString(TOURS, Integer.toString(toursCount - 1));
        editor.apply();
        for (int i = tourNumber + 1; i < toursCount; ++i) {
            Tour tour = loadTour(context, i);
            saveTour_by_number(tour, context, i - 1);
        }
    }

    public static void removeTourOld(Context context, int tourNumber) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        int taskCount = Integer.parseInt(prefs.getString(TOURS + "_" + tourNumber, "0"));
        for (int taskNumber = 0; taskNumber < taskCount + 2; ++taskNumber) {
            editor.remove(TOURS + "_" + tourNumber + "_" + taskNumber);
        }
        editor.remove(TOURS + "_" + tourNumber);
        int toursCount = Integer.parseInt(prefs.getString(TOURS, "0"));
        editor.putString(TOURS, Integer.toString(toursCount - 1));
        editor.apply();
        for (int i = tourNumber + 1; i < toursCount; ++i) {
            Tour tour = loadTour(context, i);
            saveTour_by_number(tour, context, i - 1);
        }
    }

    public static void saveTour_by_number(Tour tour, Context context, int tourNumber) {
        SharedPreferences.Editor editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        String tourNumberString = Integer.toString(tourNumber);
        String serializedTour = tour.serialize();
        editor.putString(TOURS + "_" + tourNumberString, serializedTour);
        editor.apply();
    }
}
