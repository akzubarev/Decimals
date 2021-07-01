package com.education4all.MathCoachAlg.Tours;

import android.content.Context;
import android.util.Log;

import com.education4all.MathCoachAlg.DataReader;
import com.education4all.MathCoachAlg.Tasks.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

public class TaskQueue {
    Context context;
    ObjectMapper objectMapper;
    ArrayList<QueueItem> items = new ArrayList<>();
    ArrayList<QueueItem> activeitems = new ArrayList<>();
    ArrayList<QueueItem> readyitems = new ArrayList<>();

    public TaskQueue(int[][] allowedTasks, Context context) {
        this.context = context;
        objectMapper = new ObjectMapper();
        // objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        load();
        activateTasks(allowedTasks);
    }

    public void Add(Task task) {
        int idx = IndexOf(task);

        if (idx != -1)
            activeitems.get(idx).reInit();
        else {
            QueueItem newitem = new QueueItem(task);
            items.add(newitem);
            activeitems.add(newitem);
        }
    }

    public int IndexOf(Task task) {
        for (QueueItem item : activeitems)
            if (item.task.getExpression().equals(task.getExpression()))
                return activeitems.indexOf(item);
        return -1;
    }

    public void save() {
        try {
            String json = objectMapper.writeValueAsString(items);
            DataReader.SaveQueue(json, context);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        String json = DataReader.GetQueue(context);
        if (!json.isEmpty()) {
            try {
                items = objectMapper.readValue(json, new TypeReference<ArrayList<QueueItem>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void activateTasks(int[][] allowedTasks) {
        for (int operation = 0; operation < allowedTasks.length; operation++) {
            if (allowedTasks[operation].length > 0)
                for (int complexity : allowedTasks[operation])
                    activate(operation, complexity);
        }
    }

    void activate(int operation, int complexity) {
        for (QueueItem item : items)
            if (item.validForTour(operation, complexity))
                activeitems.add(item);
    }

    void checkReady() {
        for (QueueItem item : activeitems)
            if (item.isReady() && !readyitems.contains(item))
                readyitems.add(item);
    }

    void decreaseDelay() {
        for (QueueItem item : activeitems)
            item.decreaseDelay();
    }

    public Task newTask() {

        checkReady();

        if (readyitems.size() == 0) {
            decreaseDelay();
            return Task.makeTask();
        } else
            return getReadyTask();
    }

    Task getReadyTask() {
        QueueItem item = readyitems.get(0);
        Task out = item.show();

        readyitems.remove(0);

        if (item.isFinished()) {
            items.remove(item);
            activeitems.remove(item);
        }
        return out;
    }

}
