package com.education4all.MathCoachAlg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class DataStorage {
    private final String settingsPath = "./settings.stf";
    //	private String logPath;
//	private	String erLogPath;
    public int[][] allowedTasks;
    public double tourTime, disapTime;
    public int timerstate, layoutstate, buttonsplace;

    public DataStorage() {
        allowedTasks = new int[][]{{0}, {0}, {0}, {0}};
    }


    public void loadSettings() throws IOException {
        // ��������� ��������� �� �����
        File settingsFile = new File(settingsPath);
        if (settingsFile.exists()) {
            BufferedReader fileReader = new BufferedReader(new FileReader(settingsFile));
            String line = fileReader.readLine();
            tourTime = Double.parseDouble(line);
            line = fileReader.readLine();
            disapTime = Double.parseDouble(line);
            line = fileReader.readLine();
            timerstate = Integer.parseInt(line);
            line = fileReader.readLine();
            layoutstate = Integer.parseInt(line);
            line = fileReader.readLine();
            buttonsplace = Integer.parseInt(line);
            ArrayList<int[]> buf = new ArrayList<>();
            for (line = fileReader.readLine(); line != null; line = fileReader.readLine()) {
                buf.add(LineToArr(line));
            }
            allowedTasks = new int[buf.size()][4];
            buf.toArray(allowedTasks);
            buf.toArray(allowedTasks);
            fileReader.close();
        } else {
            //allowedTasks = new int[][]{{0},{0},{0},{0}};
            allowedTasks = new int[4][];
            allowedTasks[0] = new int[4];
            allowedTasks[0][1] = 0;
            allowedTasks[1] = new int[4];
            allowedTasks[1][1] = 0;

            disapTime = -1;
            tourTime = 5 * 60;
            timerstate = 1;
            layoutstate = 0;
            buttonsplace = 0;
            saveSettings();
        }
    }

    public void saveSettings() throws IOException {
        // ��������� ��������� � ����
        File settingsFile = new File(settingsPath);
        if (!settingsFile.exists()) {
            settingsFile.createNewFile();
        }
        FileWriter fw = new FileWriter(settingsFile);
        PrintWriter pw = new PrintWriter(fw, true);
        String line = Double.toString(tourTime);
        pw.println(line);
        line = Double.toString(disapTime);
        pw.println(line);
        line = Integer.toString(timerstate);
        pw.println(line);
        line = Integer.toString(layoutstate);
        pw.println(line);
        line = Integer.toString(buttonsplace);
        pw.println(line);
        for (int[] allowedTask : allowedTasks) {
            line = "";
            for (int j = 0; j < allowedTask.length; ++j) {
                line += Integer.toString(allowedTask[j]) + ' ';
            }
            pw.println(line);
        }
        pw.close();
    }

    public static int[] LineToArr(String s) {
        ArrayList<Integer> buf = new ArrayList<>();
        int i = 0;
        int pos = s.indexOf(' ');
        int tmp;
        while (pos != -1) {
            tmp = Integer.parseInt(s.substring(i, pos));
            buf.add(tmp);
            i = pos + 1;
            pos = s.indexOf(' ', i);
        }

        return convertIntegers(buf);
    }

    public static int[] convertIntegers(ArrayList<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i);
        }
        return ret;
    }
}
