package com.education4all.mathcoach.MathCoachAlg;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Александр on 23.04.2015.
 */
public class DataReader {
    public static final String COMPLEXITY_SETTINGS = "ComplexitySettings";
    public static final String ROUND_TIME_SETTINGS ="RoundTimeSettings";

    public static int[] convertIntegers(ArrayList<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i);
        }
        return ret;
    }

    static private int[] StrToIntArr(String savedString) {
        StringTokenizer st = new StringTokenizer(savedString, ",");
        ArrayList<Integer> savedList = new ArrayList<Integer>();
        int i = 0;
        while(st.hasMoreTokens()) {
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

       static public boolean checkComplexity(int p_action, int p_complexity, Context p_context ) {
           int[][] l_allowedTasks = DataReader.readAllowedTasks(p_context);
           for (int i = 0; i < l_allowedTasks[p_action].length; ++i) {
               if (l_allowedTasks[p_action][i] == p_complexity) {
                   return true;
               }
           }
           return false;
       }

        static public void SaveRoundTime(float p_time, Context p_context) {
            SharedPreferences.Editor editor = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit();
            editor.putFloat("time", p_time);
            editor.commit();
        }

        static public float GetRoundTime(Context p_context) {
            SharedPreferences prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE);
            return prefs.getFloat("time", 1);
        }

        static public void SaveDisapRoundTime(float p_time, Context p_context) {
            SharedPreferences.Editor editor = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit();
            editor.putFloat("Disaptime", p_time);
            editor.commit();
        }

        static public float GetDisapRoundTime(Context p_context) {
            SharedPreferences prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE);
            return prefs.getFloat("Disaptime", -1);
        }
}
