package com.education4all

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.education4all.utils.Utils

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        val action = intent.action
        when (action) {
            NotificationHelper.Companion.CLOSE -> {
                notificationHelper.cancel(NotificationHelper.Companion.NOTIFICATION_ID)
                notificationHelper.repeat()
            }
            NotificationHelper.Companion.DELAY -> {
                notificationHelper.cancel(NotificationHelper.Companion.NOTIFICATION_ID)
                notificationHelper.delay()
            }
            NotificationHelper.Companion.MAKE -> {
                if (!Utils.reachedGoal(context)) notificationHelper.createNotification()
                notificationHelper.repeat()
            }
            NotificationHelper.Companion.MAKEDELAYED -> if (!Utils.reachedGoal(context)) notificationHelper.createNotification()
        }
    }
}