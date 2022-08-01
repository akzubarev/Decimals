package com.education4all

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.education4all.activities.MainActivity
import com.education4all.mathCoachAlg.DataReader
import java.text.SimpleDateFormat
import java.util.*

class NotificationHelper(private val context: Context?) {
    fun createNotification() {
        val mBuilder = configureBuilder()
        val nm = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) postOreoOptions(mBuilder, nm)
        assert(nm != null)
        nm.notify(NOTIFICATION_ID, mBuilder.build())
    }

    private fun configureBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) //                        .setContentTitle("Напоминание")
            .setContentText("Цель на день еще не выполнена")
            .setAutoCancel(true)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(makeIntent(OPEN))
            .addAction(R.drawable.ic_update, "Отложить на 10 мин.", makeIntent(DELAY))
            .addAction(R.drawable.ic_trash, "Отменить", makeIntent(CLOSE))
    }

    private fun postOreoOptions(mBuilder: NotificationCompat.Builder, nm: NotificationManager?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME", importance
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            assert(nm != null)
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            nm!!.createNotificationChannel(notificationChannel)
        }
    }

    fun setReminder(calendar: Calendar, byUser: Boolean, intentName: String) {
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarmManager != null) {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,  //                    AlarmManager.INTERVAL_DAY,
                makeIntent(intentName)
            )
            val sdf = SimpleDateFormat("HH:mm dd.MM")
            if (byUser) Toast.makeText(
                context,
                "Напомним " + sdf.format(calendar.time),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    fun setReminder(hour: Int, minute: Int, intentName: String) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute
        calendar[Calendar.SECOND] = 0
        if (calendar.time.compareTo(Date()) < 0) calendar.add(Calendar.DAY_OF_MONTH, 1)
        setReminder(calendar, true, intentName)
    }

    fun repeat() {
        val time = DataReader.GetString(DataReader.REMINDER_TIME, context)!!
            .split(":").toTypedArray()
        val hour = time[0].toInt()
        val minute = time[0].toInt()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute
        calendar[Calendar.SECOND] = 0
        setReminder(calendar, false, MAKE)
    }

    fun delay() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 10)
        setReminder(calendar, true, MAKEDELAYED)
    }

    fun cancelReminder() {
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(makeIntent(MAKE))
    }

    fun makeIntent(name: String): PendingIntent {
        return if (name == OPEN) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.action = name
            PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            val intent = Intent(context, NotificationReceiver::class.java)
            intent.action = name
            PendingIntent.getBroadcast(
                context, 1, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    fun cancel(notificationId: Int) {
        val nm = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(notificationId)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "10001"
        const val CLOSE = "close"
        const val DELAY = "delay"
        const val MAKE = "make"
        const val MAKEDELAYED = "makedelayed"
        const val OPEN = "open"
        const val NOTIFICATION_ID = 0
    }
}