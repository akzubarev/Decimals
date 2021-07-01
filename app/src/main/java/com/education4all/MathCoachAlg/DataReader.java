package com.education4all.MathCoachAlg;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


public class DataReader {
    public static final String COMPLEXITY_SETTINGS = "ComplexitySettings";
    public static final String ROUND_TIME_SETTINGS = "RoundTimeSettings";
    private static final HashMap<String, Integer> defaultValues = new HashMap<>();

    static {
        defaultValues.put("RoundTime", 1);
        defaultValues.put("DisapRoundTime", -1);
        defaultValues.put("TimerState", 1);
        defaultValues.put("ButtonsPlace", 0);
        defaultValues.put("LayoutState", 0);
        defaultValues.put("Goal", 5);
        defaultValues.put("Theme", -1);
    }

    public static int[] convertIntegers(ArrayList<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i);
        }
        return ret;
    }

    static private int[] StrToIntArr(String savedString) {
        StringTokenizer st = new StringTokenizer(savedString, ",");
        ArrayList<Integer> savedList = new ArrayList<>();
        int i = 0;
        while (st.hasMoreTokens()) {
            savedList.add(Integer.parseInt(st.nextToken()));
        }
        return convertIntegers(savedList);
    }

    static public int[][] readAllowedTasks(Context p_context) {
        SharedPreferences prefs = p_context.getSharedPreferences(COMPLEXITY_SETTINGS, Context.MODE_PRIVATE);
        String savedString = "";
        int[] Add;
        if (prefs.contains("Add")) {
            savedString = prefs.getString("Add", null);
            Add = StrToIntArr(savedString);
        } else {
            Add = new int[0];
        }
        int[] Sub;
        if (prefs.contains("Sub")) {
            savedString = prefs.getString("Sub", null);
            Sub = StrToIntArr(savedString);
        } else {
            Sub = new int[0];
        }
        int[] Mul;
        if (prefs.contains("Mul")) {
            savedString = prefs.getString("Mul", null);
            Mul = StrToIntArr(savedString);
        } else {
            Mul = new int[0];
        }
        int[] Div;
        if (prefs.contains("Div")) {
            savedString = prefs.getString("Div", null);
            Div = StrToIntArr(savedString);
        } else {
            Div = new int[0];
        }


        return new int[][]{Add, Sub, Mul, Div};
    }

    static public boolean checkComplexity(int p_action, int p_complexity, Context p_context) {
        int[][] l_allowedTasks = DataReader.readAllowedTasks(p_context);
        for (int i = 0; i < l_allowedTasks[p_action].length; ++i) {
            if (l_allowedTasks[p_action][i] == p_complexity) {
                return true;
            }
        }
        return false;
    }

    public static void SaveValue(int value, String name, Context p_context) {
        SharedPreferences.Editor editor = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putInt(name, value);
        editor.commit();
    }

    static public int GetValue(String name, Context p_context) {
        SharedPreferences prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE);
        return prefs.getInt(name, defaultValues.get(name));
    }

    public static void SaveQueue(String json, Context p_context) {
        SharedPreferences.Editor editor = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString("Queue", json);
        editor.commit();
    }

    static public String GetQueue(Context p_context) {
        SharedPreferences prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE);
        return prefs.getString("Queue", "");
    }

    public static void SaveStat(String json, Context p_context) {
        SharedPreferences.Editor editor = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString("Stat", json);
        editor.commit();
    }

    static public String GetStat(Context p_context) {
        SharedPreferences prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE);
        return prefs.getString("Stat", "");
    }

}
