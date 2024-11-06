package com.example.adminbuyify.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.adminbuyify.AdminMainActivity
import com.example.adminbuyify.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val channelID = "AdminBuyify"
        val channel = NotificationChannel(channelID, "Buyify", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Buyify messages"
            enableLights(true)
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager
        manager.createNotificationChannel(channel)
        val pendingIntent = PendingIntent.getActivity(this,0, Intent(this, AdminMainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(Random.nextInt() , notification)

    }

}