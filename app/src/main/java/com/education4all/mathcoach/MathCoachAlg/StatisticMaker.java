package com.education4all.mathcoach.MathCoachAlg;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by Александр on 03.05.2015.b
 */
public class StatisticMaker {

    public static final String STATISTICS = "Statistics";
    public static final String TOURS = "tours";

    public static void saveTour(Tour p_Tour, Context p_context) {
        SharedPreferences.Editor l_editor = p_context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = p_context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        String tourCountStr = prefs.getString(TOURS, "0");
        int tourCount = Integer.parseInt(tourCountStr);
        l_editor.putString(TOURS + "_" + tourCountStr, Integer.toString(p_Tour.totalTasks));
        ArrayList<String> serializedTour = p_Tour.serialize();
        for (int taskNumber = 0; taskNumber < p_Tour.totalTasks + 2; ++taskNumber) {
            l_editor.putString(TOURS + "_" + tourCountStr + "_" + Integer.toString(taskNumber),serializedTour.get(taskNumber));
        }
        l_editor.putString(TOURS, Integer.toString(tourCount + 1));
        l_editor.apply();
    }

    public static ArrayList<Tour> loadTours(Context p_context) {
        ArrayList<Tour> resultTours = new ArrayList<Tour>();
        SharedPreferences prefs = p_context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        int toursCount = Integer.parseInt(prefs.getString(TOURS, "0"));
        for (int tourNumber = 0; tourNumber < toursCount; ++tourNumber) {
            int taskCount = Integer.parseInt(prefs.getString(TOURS + "_" + tourNumber, "0"));
            ArrayList<String> currentTour = new ArrayList<String>();

            for (int taskNumber = 0; taskNumber < taskCount; ++taskNumber) {
                currentTour.add(prefs.getString(TOURS + "_" + tourNumber + "_" + taskNumber, "0"));
            }
            if (currentTour.size() > 0) {
                Tour currentT_Tour = new Tour(currentTour);

                resultTours.add(currentT_Tour);
            }
        }
        return resultTours;
    }

    public static Tour loadTour(Context p_context, int tourNumber) {
        SharedPreferences prefs = p_context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        int taskCount = Integer.parseInt(prefs.getString(TOURS + "_" + tourNumber, "0"));
        ArrayList<String> currentTour = new ArrayList<String>();
        for (int taskNumber = 0; taskNumber < taskCount + 2; ++taskNumber) {
            currentTour.add(prefs.getString(TOURS + "_" + tourNumber + "_" + taskNumber, "0"));
        }
        if (currentTour.size() > 0) {
            Tour currentT_Tour = new Tour(currentTour);

            return currentT_Tour;
        } else {
            return null;
        }
    }

    public static int getTourCount(Context p_context) {
        SharedPreferences prefs = p_context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        int toursCount = Integer.parseInt(prefs.getString(TOURS, "0"));
        return toursCount;
    }

    public static String getTourInfo(Context p_context, int tourNumber) {
        SharedPreferences prefs = p_context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        return prefs.getString(TOURS + "_" + tourNumber + "_" + "0", "");
    }

    public static void removeStatistics(Context p_context){
        SharedPreferences.Editor l_editor = p_context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        l_editor.clear();
        l_editor.apply();
    }

    public static void removeTour(Context p_context, int tourNumber) {
        SharedPreferences.Editor l_editor = p_context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = p_context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        int taskCount = Integer.parseInt(prefs.getString(TOURS + "_" + tourNumber, "0"));
        for (int taskNumber = 0; taskNumber < taskCount + 2; ++taskNumber) {
            l_editor.remove(TOURS + "_" + tourNumber + "_" + taskNumber);
        }
        l_editor.remove(TOURS + "_" + tourNumber);
        int toursCount = Integer.parseInt(prefs.getString(TOURS, "0"));
        l_editor.putString(TOURS, Integer.toString(toursCount - 1));
        l_editor.apply();
        for (int i = tourNumber + 1; i < toursCount; ++i) {
            Tour l_tour = loadTour(p_context, i);
            saveTour_by_number(l_tour, p_context, i - 1);
        }
    }

    public static void saveTour_by_number(Tour p_Tour, Context p_context, int tourNumber) {
        SharedPreferences.Editor l_editor = p_context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = p_context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE);
        String tourNumberString = Integer.toString(tourNumber);
        l_editor.putString(TOURS + "_" + tourNumberString, Integer.toString(p_Tour.totalTasks));
        ArrayList<String> serializedTour = p_Tour.serialize();
        for (int taskNumber = 0; taskNumber < p_Tour.totalTasks + 2; ++taskNumber) {
            l_editor.putString(TOURS + "_" + tourNumberString + "_" + Integer.toString(taskNumber),serializedTour.get(taskNumber));
        }
        l_editor.apply();
    }
}
