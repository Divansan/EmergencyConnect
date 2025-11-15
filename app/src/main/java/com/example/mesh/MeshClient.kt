package com.example.mesh


import android.content.Context
import android.util.Log
import java.io.*
import java.net.Socket
import java.util.concurrent.Executors

class MeshClient(private val context: Context) {

    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: PrintWriter? = null
    private val executor = Executors.newSingleThreadExecutor()

    companion object {
        const val TAG = "MeshClient"
    }

    interface ClientListener {
        fun onConnected()
        fun onMessageReceived(message: String)
        fun onError(error: String)
    }

    private var listener: ClientListener? = null

    fun initialize(listener: ClientListener) {
        this.listener = listener
    }

    fun connectToServer(serverAddress: String, port: Int = MeshServer.SERVER_PORT) {
        executor.execute {
            try {
                socket = Socket(serverAddress, port)
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                writer = PrintWriter(OutputStreamWriter(socket!!.getOutputStream()), true)

                Log.d(TAG, "Connected to mesh server: $serverAddress")
                listener?.onConnected()

                // Start listening for messages
                listenForMessages()
            } catch (e: IOException) {
                Log.e(TAG, "Error connecting to mesh server", e)
                listener?.onError("Connection failed: ${e.message}")
            }
        }
    }

    private fun listenForMessages() {
        executor.execute {
            try {
                var message: String?
                while (reader?.readLine().also { message = it } != null) {
                    message?.let {
                        Log.d(TAG, "Received message from server: $it")
                        listener?.onMessageReceived(it)
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error reading messages", e)
                listener?.onError("Read error: ${e.message}")
            }
        }
    }

    fun sendMessage(message: String) {
        try {
            writer?.println(message)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
            listener?.onError("Send error: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            reader?.close()
            writer?.close()
            socket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error disconnecting", e)
        }
    }
}