package com.example.iassistdatabase.ui.notifications

data class NotificationItem(
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    var read: Boolean = false,
    var key: String = "" // 🔹 Store Firebase key here
)
