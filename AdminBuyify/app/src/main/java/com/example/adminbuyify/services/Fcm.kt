package com.example.adminbuyify.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.adminbuyify.activity.AdminMainActivity
import com.example.adminbuyify.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class Fcm : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val channelID = "AdminBuyify"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelID, "Buyify", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Buyify messages"
                enableLights(true)
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, AdminMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle(message.data["title"] ?: "New Notification")
            .setContentText(message.data["body"] ?: "You have a new message")
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(Random.nextInt(), notification)
    }


}