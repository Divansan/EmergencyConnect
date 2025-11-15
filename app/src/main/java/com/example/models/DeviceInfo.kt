package com.example.models

data class DeviceInfo(
    val name: String,
    val distance: String,
    val type: String,
    val lastSeen: String,
    val deviceId: String = "",
    val isConnected: Boolean = false,
    val signalStrength: Int = 0
)