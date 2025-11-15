package com.example.models

import java.util.UUID

data class MeshMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val senderName: String,
    val messageType: MessageType,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val location: LocationData? = null,
    val priority: Priority = Priority.NORMAL
) {
    enum class MessageType {
        TEXT, SOS, LOCATION, STATUS, EMERGENCY_ALERT
    }

    enum class Priority {
        LOW, NORMAL, HIGH, CRITICAL
    }
}

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f
)