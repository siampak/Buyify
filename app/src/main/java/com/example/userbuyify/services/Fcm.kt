package com.example.userbuyify.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.userbuyify.R
import com.example.userbuyify.activity.UsersMainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


// Utility function to sanitize strings for Firebase path
fun sanitizePath(input: String): String {
    // Replace invalid characters with an underscore
    return input.replace(Regex("[.#$\\[\\]]"), "_")
}

class Fcm : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val channelID = "UserBuyify"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                "Buyify",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Buyify messages"
                enableLights(true)
            }
            manager.createNotificationChannel(channel)
        }

        // Retrieve title and body from the message, sanitize them to avoid invalid characters
        val rawTitle = message.data["title"] ?: "New Notification"
        val rawBody = message.data["body"] ?: "You have a new message"

        // Sanitize title and body before using them in the notification
        val title = sanitizePath(rawTitle)
        val body = sanitizePath(rawBody)


        val intent = Intent(this, UsersMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(Random.nextInt(), notification)
    }


}


/*
class Fcm : FirebaseMessagingService() {

override fun onMessageReceived(message: RemoteMessage) {
super.onMessageReceived(message)

val channelID = "UserBuyify"
val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
val channel = NotificationChannel(
channelID,
"Buyify",
NotificationManager.IMPORTANCE_HIGH
).apply {
description = "Buyify messages"
enableLights(true)
}
manager.createNotificationChannel(channel)
}

val title = message.data["title"] ?: "New Notification"
val body = message.data["body"] ?: "You have a new message"

// Prevent notifications from admin app
if (isAdminApp()) {
return
}

// Validate the title and body before creating a notification
if (isValidPath(title) && isValidPath(body)) {
val intent = Intent(this, UsersMainActivity::class.java).apply {
flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
}
val pendingIntent = PendingIntent.getActivity(
this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

val notification = NotificationCompat.Builder(this, channelID)
.setContentTitle(title)
.setContentText(body)
.setSmallIcon(R.drawable.icon)
.setContentIntent(pendingIntent)
.setAutoCancel(true)
.build()

manager.notify(Random.nextInt(), notification)
} else {
Log.e(
"NotificationError",
"Invalid notification data: Title or Body contains invalid characters."
)
}
}

private fun isValidPath(path: String): Boolean {
val invalidCharacters = listOf('.', '#', '$', '[', ']')
return !invalidCharacters.any { path.contains(it) }
}

private fun isAdminApp(): Boolean {
// Implement your logic to check if the request is from the admin app
return false // Change as needed
}
}
*/

