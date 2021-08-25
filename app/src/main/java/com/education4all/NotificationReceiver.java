package com.education4all;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.education4all.activities.MainActivity;
import com.education4all.mathCoachAlg.DataReader;
import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tasks.Task;
import com.education4all.mathCoachAlg.tours.Tour;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);

        if (!Utils.reachedGoal(context))
            notificationHelper.createNotification();

        notificationHelper.repeat();
    }
}
