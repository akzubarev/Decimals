package com.education4all;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Pair;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.education4all.R;
import com.education4all.mathCoachAlg.DataReader;
import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tasks.SerializableTask;
import com.education4all.mathCoachAlg.tasks.Task;
import com.education4all.mathCoachAlg.tours.Tour;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Utils {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    static ArrayList<Tour> oldtours = new ArrayList<>();

    public static void FixDialog(AlertDialog dialog, Context context) {
//        Resources res = context.getResources();
//        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
//        if (textView != null) {
//            textView.setTextSize(res.getDimension(R.dimen.dimen4) / res.getDisplayMetrics().density);
//            textView.setTypeface(Typeface.create("sans-serif-light", 0));
//            int colormain = res.getColor(R.color.main);
//            Button btn = dialog.getButton(-2);
//            if (btn != null) {
//                btn.setTextColor(colormain);
//            }
//            Button btn2 = dialog.getButton(-1);
//            if (btn2 != null) {
//                btn2.setTextColor(colormain);
//            }
//            Button btn3 = dialog.getButton(-3);
//            if (btn3 != null) {
//                btn3.setTextColor(colormain);
//                return;
//            }
//            return;
//        }
//        throw new AssertionError();
    }

    public static void save(Context context) {
        oldtours = StatisticMaker.loadToursOLD(context);
        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(oldtours);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DataReader.SaveStat(json, context);
    }

    public static void load(Context context) {
        if (oldtours.size() > 0) {
            Iterator<Tour> it = oldtours.iterator();
            while (it.hasNext()) {
                StatisticMaker.saveTourOLD(it.next(), context);
            }
            return;
        }
        try {
            ArrayList<Tour> arrayList = (ArrayList) new ObjectMapper().readValue(DataReader.GetStat(context), (TypeReference) new TypeReference<ArrayList<Tour>>() {
            });
            oldtours = arrayList;
            Iterator<Tour> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                StatisticMaker.saveTourOLD(it2.next(), context);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean requiresKostyl(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar newversiondate = Calendar.getInstance();
        Calendar currentdate = Calendar.getInstance();
        try {
            newversiondate.setTime(sdf.parse("05.04.2021"));
            currentdate.setTime(sdf.parse(date));
            if (currentdate.before(newversiondate)) {
                return true;
            }
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void genereteFakeStatistics(Context context, boolean old) {
        for (String stat : new String[]{"1 0 0 1 1 1 1 1 1 1 1 0", "2 1 1 1 1 1 1 1 1 1 0 1", "3 1 1 1 1 1 1 1 0 1 1 1", "4 1 0 1 1 1 1 1 1 0 1 1", "5 1 0 1 1 1 1 0 1 1 1 1", "6 1 1 1 1 1 1 1 0 1 1 1", "7 0 1 1 1 1 1 1 1 1 0 1", "8 1 0 1 1 0 1 1 1 1 1 0", "9 1 1 1 1 1 1 1 1 1 1 0", "10 1 1 1 1 1 0 1 1 1 0 0"}) {
            generateFakeTour(stat, context, old);
        }
    }

    public static void generateFakeTour(String stat, Context context, boolean old) {
        if (old) {
            StatisticMaker.saveTourOLD(genTour(stat, true), context);
        } else {
            StatisticMaker.saveTour(genTour(stat, false), context);
        }
    }

    private static Tour genTour(String stat, boolean old) {
        Random rnd = new Random();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(5, -31);
        String[] lines = stat.split(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
        c.add(5, Integer.parseInt(lines[0]));
        Tour tour = new Tour();
        tour.setTourDateTime(c.getTimeInMillis());
        tour.setTourTime(0);
        for (int j = 1; j < lines.length; j++) {
            Task task = Task.makeTask();
            task.setTimeTaken((long) rnd.nextInt(100));
            tour.setTourTime(tour.getTourTime() + task.getTimeTaken());
            boolean z = true;
            if (Integer.parseInt(lines[j]) == 1) {
                if (old) {
                    task.makeUserAnswer(task.getAnswer() + ":" + rnd.nextInt(20) + '|', "");
                } else {
                    task.makeUserAnswer(task.getAnswer(), String.valueOf(rnd.nextInt(20)));
                }
            } else if (old) {
                task.makeUserAnswer("…:" + rnd.nextInt(20) + '|', "");
            } else {
                task.makeUserAnswer("…", String.valueOf(rnd.nextInt(20)));
            }
            if (Integer.parseInt(lines[j]) != 1) {
                z = false;
            }
            tour.addTaskOld(task, z);
        }
        return tour;
    }

    public static void updateStatistics(Context context) {
        String txt;
        Context context2 = context;
        if (StatisticMaker.getTourCount(context) > 0) {
            ArrayList<Tour> update = new ArrayList<>();
            for (int tourNumber = 0; tourNumber < StatisticMaker.getTourCount(context); tourNumber++) {
                String tourInfoStr = StatisticMaker.getTourInfoOLD(context2, tourNumber);
                if (!tourInfoStr.isEmpty()) {
                    String txt2 = Tour.DepictTour(tourInfoStr);
                    int divider = txt2.indexOf("Решено");
                    String datetime = txt2.substring(1, divider - 1);
                    String date = datetime.substring(0, 10);
                    Tour oldtour = StatisticMaker.loadTourOld(context2, tourNumber);
                    Tour newTour = new Tour();
                    newTour.copy(oldtour);
                    if (requiresKostyl(date)) {
                        newTour.getTourTasks().clear();
                        ArrayList<String> deTour = oldtour.serializeOLD();
                        int jump = 0;
                        int i = 1;
                        for (int i2 = 1; i < deTour.size() - i2; i2 = 1) {
                            ArrayList<String> answers = new ArrayList<>();
                            Task currentTask = Task.makeTask(deTour.get(i));
                            ArrayList<String> TaskDepiction = Task.DepictTaskExtended(deTour.get(i), answers);
                            String tourInfoStr2 = tourInfoStr;
                            Task newTask = Task.makeTask();
                            newTask.copy(currentTask);
                            newTask.update();
                            newTour.addTask(newTask);
                            if (requiresKostyl(date)) {
                                Task task = newTask;
                                int j = 0;
                                while (true) {
                                    txt = txt2;
                                    if (j >= TaskDepiction.size()) {
                                        break;
                                    }
                                    int divider2 = divider;
                                    boolean answerIsCorrect = answers.get(j).equals(currentTask.getAnswer());
                                    String datetime2 = datetime;
                                    boolean taskWasSkipped = answers.get(j).equals("…");
                                    if (answerIsCorrect || taskWasSkipped) {
                                        i += jump;
                                        jump = 0;
                                    } else {
                                        jump++;
                                    }
                                    boolean z = answerIsCorrect;
                                    if (i + jump == deTour.size() - 2) {
                                        i += jump;
                                    }
                                    j++;
                                    txt2 = txt;
                                    divider = divider2;
                                    datetime = datetime2;
                                }
                            } else {
                                txt = txt2;
                            }
                            i++;
                            tourInfoStr = tourInfoStr2;
                            txt2 = txt;
                            divider = divider;
                            datetime = datetime;
                        }
                        String str = txt2;
                        int i3 = divider;
                        String str2 = datetime;
                    } else {
                        String str3 = txt2;
                        int i4 = divider;
                        String str4 = datetime;
                        Iterator<SerializableTask> it = newTour.getTourTasks().iterator();
                        while (it.hasNext()) {
                            it.next().update();
                        }
                    }
                    update.add(newTour);
                } else {
                    update.add(StatisticMaker.loadTour(context2, tourNumber));
                }
            }
            StatisticMaker.removeStatistics(context);
            Iterator<Tour> it2 = update.iterator();
            while (it2.hasNext()) {
                StatisticMaker.saveTour(it2.next(), context2);
            }
        }
    }

    public static int randomly_select(List<Pair<Integer, Integer>> list) {
        int sum = 0;
        for (Pair<Integer, Integer> item : list) {
            sum += ((Integer) item.second).intValue();
        }
        int index = new Random().nextInt(sum) + 1;
        int sum2 = 0;
        for (Pair<Integer, Integer> item2 : list) {
            sum2 += ((Integer) item2.second).intValue();
            if (sum2 >= index) {
                return ((Integer) item2.first).intValue();
            }
        }
        return -1;
    }
}
