package com.team.squadx

data class NotificationModel(
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val level: String = "",
    val branch: String = "",
    val section: String = ""
)
