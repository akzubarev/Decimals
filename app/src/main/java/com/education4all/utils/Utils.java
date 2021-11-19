package com.education4all.utils;

import android.content.Context;
import android.util.Pair;

import androidx.appcompat.app.AlertDialog;

import com.education4all.BuildConfig;
import com.education4all.mathCoachAlg.DataReader;
import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tours.Tour;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Utils {

    public static void FixDialog(AlertDialog dialog, Context context) {
//        Resources res = context.getResources();
//        TextView textView = dialog.findViewById(android.R.id.message);
//        textView.setTextSize(res.getDimension(R.dimen.dimen4) / res.getDisplayMetrics().density);
//        textView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
//
//        int colormain = res.getColor(R.color.main);
//        Button btn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//        if (btn != null)
//            btn.setTextColor(colormain);
//        btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        if (btn != null)
//            btn.setTextColor(colormain);
//        btn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
//        if (btn != null)
//            btn.setTextColor(colormain);
    }

    public static int randomly_select(List<Pair<Integer, Integer>> list) {
        int sum = 0;
        for (Pair<Integer, Integer> item : list)
            sum += item.second;

        int index = new Random().nextInt(sum) + 1;
        sum = 0;
        for (Pair<Integer, Integer> item2 : list) {
            sum += item2.second;
            if (sum >= index)
                return item2.first;
        }
        return -1;
    }

    public static boolean reachedGoal(Context context) {
        int secondstoday = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        String today = sdf.format(c.getTime());

        if (StatisticMaker.getTourCount(context) > 0) {
            for (int tourNumber = 0; tourNumber < StatisticMaker.getTourCount(context); tourNumber++) {

                Tour tour = StatisticMaker.loadTour(context, tourNumber);
                if (tour.getTotalTasks() == 0)
                    return false;

                int seconds = (int) tour.getTourTime();
                String date = tour.date();
                if (Enums.isSubsequent(date, today) == Enums.twodates.equal)
                    secondstoday += seconds;
            }

            int goal = DataReader.GetInt(DataReader.GOAL, context) * 60;
            return secondstoday > goal;
        } else
            return false;
    }

    public static String versioningTool() {
        String version = "other";
        if (BuildConfig.FLAVOR.equals("decimals") &&
                BuildConfig.VERSION_CODE <= 3)
            version = "decimalsBeta";
        return version;
    }
//        TextView statistics = findViewById(R.id.statistics);
//        SpannableString ss = new SpannableString("Разные шрифты");
//
//        Typeface font = Typeface.DEFAULT_BOLD,
//                font2 = Typeface.MONOSPACE;
//
//        ss.setSpan(new CustomTypefaceSpan("", font2), 0, 6, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//        ss.setSpan(new CustomTypefaceSpan("", font), 6, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//        statistics.setText(ss);
}