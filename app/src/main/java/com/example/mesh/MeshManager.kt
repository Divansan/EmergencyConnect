package com.example.mesh


import android.content.Context
import android.util.Log
import com.example.models.MeshMessage

class MeshManager(private val context: Context) {

    private val meshServer: MeshServer by lazy { MeshServer(context) }
    private val meshClient: MeshClient by lazy { MeshClient(context) }

    companion object {
        const val TAG = "MeshManager"
    }

    interface MeshListener {
        fun onMessageReceived(message: MeshMessage)
        fun onDeviceConnected(deviceId: String)
        fun onDeviceDisconnected(deviceId: String)
        fun onError(error: String)
    }

    private var listener: MeshListener? = null

    fun initialize(listener: MeshListener) {
        this.listener = listener
        meshServer.initialize(object : MeshServer.ServerListener {
            override fun onClientConnected(clientId: String) {
                listener.onDeviceConnected(clientId)
            }

            override fun onMessageReceived(message: String) {
                // Parse and handle received message
                try {
                    // Parse JSON message to MeshMessage
                    // For now, create a simple message
                    val meshMessage = MeshMessage(
                        senderId = "unknown",
                        senderName = "Unknown",
                        messageType = MeshMessage.MessageType.TEXT,
                        content = message
                    )
                    listener.onMessageReceived(meshMessage)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing message", e)
                }
            }

            override fun onError(error: String) {
                listener.onError(error)
            }
        })

        meshClient.initialize(object : MeshClient.ClientListener {
            override fun onConnected() {
                Log.d(TAG, "Mesh client connected")
            }

            override fun onMessageReceived(message: String) {
                // Handle received messages
            }

            override fun onError(error: String) {
                listener.onError(error)
            }
        })
    }

    fun startMeshNetwork() {
        meshServer.start()
        // Client will connect when servers are discovered
    }

    fun stopMeshNetwork() {
        meshServer.stop()
        meshClient.disconnect()
    }

    fun broadcastMessage(message: MeshMessage) {
        // Convert message to string (JSON) and broadcast
        val messageString = "${message.messageType}:${message.content}"
        meshServer.broadcastMessage(messageString)
        meshClient.sendMessage(messageString)
    }

    fun sendDirectMessage(deviceId: String, message: MeshMessage) {
        // Send message to specific device
        val messageString = "${message.messageType}:${message.content}"
        // Implementation depends on your mesh protocol
    }
}