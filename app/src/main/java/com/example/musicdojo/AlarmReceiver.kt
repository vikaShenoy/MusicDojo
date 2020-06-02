package com.example.musicdojo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.getSystemService

class AlarmReceiver : BroadcastReceiver() {

    // Send the user a notification reminding them to do ear training
    override fun onReceive(ctx: Context?, intent: Intent?) {
        val notification = Notification.Builder(ctx, Notification.CATEGORY_REMINDER).run {
            setSmallIcon(R.drawable.ic_notify)
            setContentTitle("Ear Training")
            setContentText("Don't forget to do your ear training for the day")
            setAutoCancel(true)
            build()
        }
        val manager = ctx?.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        manager.notify(0, notification)
    }
}