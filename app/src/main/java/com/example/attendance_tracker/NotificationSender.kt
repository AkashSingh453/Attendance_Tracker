package com.example.attendance_tracker

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun sendNotification(activity: Activity, title: String, message: String) {
    if (!checkAndRequestNotificationPermission(activity)) {
        // Permission is not granted, return early
        return
    }

    val context = activity.applicationContext
    val channelId = "default_channel_id"
    val channelName = "Default Channel"

    // Create the notification channel for API 26+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Default Channel Description"
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Build the notification
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.iconround)  // You need to have a small icon in your drawable resources
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_LOW)

    // Show the notification
    with(NotificationManagerCompat.from(context)) {
        notify(1, builder.build())  // 1 is the notification ID
    }
}

fun cancelNotification(context: Context, notificationId: Int) {
    with(NotificationManagerCompat.from(context)) {
        cancel(notificationId)  // Cancel the notification by its ID
    }
}


const val REQUEST_CODE_POST_NOTIFICATIONS = 1001

fun checkAndRequestNotificationPermission(activity: Activity): Boolean {
    return if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            REQUEST_CODE_POST_NOTIFICATIONS
        )
        false
    } else {
        true
    }
}