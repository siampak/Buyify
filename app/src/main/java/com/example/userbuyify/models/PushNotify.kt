package com.example.userbuyify.models

data class PushNotify(
    val message: Message
)
data class Message(
    val token: String,
    val notification: Notification

)
data class Notification(
    val body: String,
    val title: String
)