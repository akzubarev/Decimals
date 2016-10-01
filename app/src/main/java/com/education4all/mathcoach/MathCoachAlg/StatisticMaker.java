package com.education4all.mathcoach.MathCoachAlg;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import MathCoachAlg.Tour;

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
        for (int taskNumber = 0; taskNumber < p_Tour.totalTasks; ++taskNumber) {
            l_editor.putString(TOURS + "_" + tourCountStr + "_" + Integer.toString(taskNumber),serializedTour.get(taskNumber));
        }
        l_editor.putString(TOURS, Integer.toString(tourCount + 1));
        l_editor.commit();
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
        for (int taskNumber = 0; taskNumber < taskCount; ++taskNumber) {
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
}