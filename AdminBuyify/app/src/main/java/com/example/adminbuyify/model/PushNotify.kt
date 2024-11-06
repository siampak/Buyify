package com.example.adminbuyify.model

data class PushNotify(
    val message: Message,
)
data class Message(
    val token: String,
    val notification: Notification  // The actual notification to send

)
data class Notification(
    val body: String,
    val title: String
)