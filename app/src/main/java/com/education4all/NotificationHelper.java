package com.education4all;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.education4all.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotificationHelper {

    private Context mContext;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    public NotificationHelper(Context context) {
        mContext = context;
    }

    void createNotification() {
        PendingIntent pendingIntent = getPendingIntent();

        NotificationCompat.Builder mBuilder = configureBuilder(pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            postOreoOptions(mBuilder, mNotificationManager);

        assert mNotificationManager != null;
        mNotificationManager.notify(0, mBuilder.build());
    }

    private NotificationCompat.Builder configureBuilder(PendingIntent pendingIntent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Напоминание")
                        .setContentText("Цель на день еще не выполнена")
                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(pendingIntent);

        return mBuilder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void postOreoOptions(NotificationCompat.Builder mBuilder, NotificationManager mNotificationManager) {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel =
                new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        "NOTIFICATION_CHANNEL_NAME",
                        importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        assert mNotificationManager != null;
        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        mNotificationManager.createNotificationChannel(notificationChannel);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setReminder(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        PendingIntent pendingIntent = getPendingIntent();
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM");
            Toast.makeText(mContext,
                    "Напомним " + sdf.format(calendar.getTime()),
                    Toast.LENGTH_SHORT)
                    .show();

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void repeat() {
        Calendar calendar = Calendar.getInstance();
        setReminder(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public void cancelReminder() {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent());
    }

    public PendingIntent getPendingIntent() {
        Intent intent = new Intent(mContext, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}


