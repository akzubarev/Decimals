package com.education4all.MathCoachAlg.Tours;

import com.education4all.MathCoachAlg.Tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Tour {
    public ArrayList<Task> tourTasks = new ArrayList<>(0);
    public int totalTasks = 0;
    public int rightTasks= 0;
    public long tourTime;
    public long tourDateTime;
    public static final String tourEndStr = "tourEnd";

    public Tour() {
        tourDateTime = System.currentTimeMillis();
    }

    public static String DepictTour(String line) {
        int found = line.indexOf(';');
        line = line.substring(found + 1);
        found = line.indexOf(';');
        long l_tourDateTime = Long.parseLong(line.substring(0, found));
        line = line.substring(found + 1);
        found = line.indexOf(';');
        int l_totalTasks = Integer.parseInt(line.substring(0, found)); // lineVect.size() - 2
        line = line.substring(found + 1);
        found = line.indexOf(';');
        int l_rightTasks = Integer.parseInt(line.substring(0, found)); // ����� ��������� � ����� (����� ��?)
        line = line.substring(found + 1);
        found = line.indexOf(';');
        long l_tourTime = Long.parseLong(line.substring(0, found));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l_tourDateTime);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy',' HH:mm");
        String str = (l_rightTasks == l_totalTasks ? "=" : "_");
        str += sdf.format(calendar.getTime()) + ". ";
        str += ((l_tourTime / 60 == 0) ? "1" : Long.toString(l_tourTime / 60)) + " мин. " + "\n";
        str += "Решено " + l_rightTasks + "/" + l_totalTasks;
        str += " (" + l_rightTasks * 100 / l_totalTasks + "%)";
        str += "сек" + l_tourTime;
        return str;
    }

    public Tour(String type, ArrayList<String> lineVect) {
        tourTasks = new ArrayList<>(0);
        String line = lineVect.get(0);
        Task myTask;
        int found = line.indexOf(';');
        line = line.substring(found + 1);
        found = line.indexOf(';');
        tourDateTime = Long.parseLong(line.substring(0, found));
        line = line.substring(found + 1);
        found = line.indexOf(';');
        totalTasks = Integer.parseInt(line.substring(0, found)); // lineVect.size() - 2
        line = line.substring(found + 1);
        found = line.indexOf(';');
        rightTasks = Integer.parseInt(line.substring(0, found)); // ����� ��������� � ����� (����� ��?)
        line = line.substring(found + 1);
        found = line.indexOf(';');
        tourTime = Long.parseLong(line.substring(0, found));
        for (int i = 1; i < lineVect.size() - 1; ++i) {
            line = lineVect.get(i);
            myTask = Task.makeTask(line);
            tourTasks.add(myTask);
        }
    }

    public ArrayList<String> serialize() {
        String line;
        ArrayList<String> result = new ArrayList<>();
        line = "tourStart;" + tourDateTime + ';' + totalTasks + ';' +
                rightTasks + ';' + tourTime + ';';
        result.add(line);
        for (int i = 0; i < tourTasks.size(); ++i) {
            result.add(tourTasks.get(i).serialize());
        }
        line = tourEndStr;
        result.add(line);
        return result;
    }

    public void deSerialize(ArrayList<String> lineVect) {
        tourTasks = new ArrayList<>(0);
        String line = lineVect.get(0);
        Task myTask;
        int found = line.indexOf(';');
        line = line.substring(found + 1);
        found = line.indexOf(';');
        tourDateTime = Long.parseLong(line.substring(0, found));
        line = line.substring(found + 1);
        found = line.indexOf(';');
        totalTasks = Integer.parseInt(line.substring(0, found)); // lineVect.size() - 2
        line = line.substring(found + 1);
        found = line.indexOf(';');
        rightTasks = Integer.parseInt(line.substring(0, found)); // ����� ��������� � ����� (����� ��?)
        line = line.substring(found + 1);
        found = line.indexOf(';');
        tourTime = Long.parseLong(line.substring(0, found));
        for (int i = 1; i < lineVect.size() - 1; ++i) {
            line = lineVect.get(i);
            myTask = Task.makeTask(line);
            tourTasks.add(myTask);
        }
    }
}
