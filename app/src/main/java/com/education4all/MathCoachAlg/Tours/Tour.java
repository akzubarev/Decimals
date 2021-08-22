package com.education4all.mathCoachAlg.tours;

import android.util.Log;

import com.education4all.mathCoachAlg.tasks.SerializableTask;
import com.education4all.mathCoachAlg.tasks.Task;
import com.education4all.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Tour {
    private ArrayList<SerializableTask> tourTasks = new ArrayList<>(0);
    private int totalTasks = 0;
    private int rightTasks = 0;
    private long tourTime;
    private long tourDateTime;

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

    public Tour(ArrayList<String> lineVect) {
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
            tourTasks.add(new SerializableTask(myTask));
        }
    }

    public String serialize() {
        String json = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public ArrayList<String> serializeOLD() {
        String line;
        ArrayList<String> result = new ArrayList<>();
        line = "tourStart;" + tourDateTime + ';' + totalTasks + ';' +
                rightTasks + ';' + tourTime + ';';
        result.add(line);
        for (int i = 0; i < tourTasks.size(); ++i) {
            result.add(tourTasks.get(i).serializeOLD());
        }
        line = "tourEnd";
        result.add(line);
        return result;
    }

    //region get/set
    public ArrayList<SerializableTask> getTourTasks() {
        return tourTasks;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getRightTasks() {
        return rightTasks;
    }

    public long getTourTime() {
        return tourTime;
    }

    public void setTourTime(long tourTime) {
        this.tourTime = tourTime;
    }

    public long getTourDateTime() {
        return tourDateTime;
    }

    public void setTourDateTime(long tourDateTime) {
        this.tourDateTime = tourDateTime;
    }

    //endregion

    public void addTask(Task task) {
        tourTasks.add(new SerializableTask(task));
        if (task.correct())
            rightTasks++;
        totalTasks++;
    }

    public void addTaskOld(Task task, boolean correct) {
        tourTasks.add(new SerializableTask(task));
        if (correct)
            rightTasks++;
        totalTasks++;
    }

    public static Tour deSerialize(String json) {
        if (!json.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(json, Tour.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void copy(Tour t) {
        tourTasks = t.getTourTasks();
        tourDateTime = t.getTourDateTime();
        totalTasks = t.getTotalTasks();
        rightTasks = t.getRightTasks();
        tourTime = t.getTourTime();
    }

    public String date() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tourDateTime);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(calendar.getTime());
    }

    public String info() {
        String out = "";

        out += "Решено " + rightTasks + "/" + totalTasks;
        out += " (" + rightTasks * 100 / totalTasks + "%)";
        return out;
    }

    public String dateTime() {
        String out = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tourDateTime);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy',' HH:mm");
        out += sdf.format(calendar.getTime()) + ". ";
        out += ((tourTime / 60 == 0) ? "1" : Long.toString(tourTime / 60)) + " мин. " + "\n";
        return out;
    }

    public void deSerializeOld(ArrayList<String> lineVect) {
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
            tourTasks.add(new SerializableTask(myTask));
        }
    }
}
