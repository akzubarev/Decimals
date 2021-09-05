package com.education4all;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.education4all.utils.Utils;

public class NotificationReceiver extends BroadcastReceiver {


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);

        String action = intent.getAction();
        switch (action) {
            case NotificationHelper.CLOSE:
                notificationHelper.cancel(NotificationHelper.NOTIFICATION_ID);
                break;
            case NotificationHelper.DELAY:
                notificationHelper.delay();
                notificationHelper.cancel(NotificationHelper.NOTIFICATION_ID);
                break;
            case NotificationHelper.MAKE:
                if (!Utils.reachedGoal(context))
                    notificationHelper.createNotification();
                break;
        }
        notificationHelper.repeat();
    }
}
