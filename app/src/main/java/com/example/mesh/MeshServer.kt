package com.example.mesh


import android.content.Context
import android.util.Log
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

class MeshServer(private val context: Context) {

    private var serverSocket: ServerSocket? = null
    private val clientHandlers = mutableListOf<ClientHandler>()
    private val executor = Executors.newCachedThreadPool()

    companion object {
        const val TAG = "MeshServer"
        const val SERVER_PORT = 8888
    }

    interface ServerListener {
        fun onClientConnected(clientId: String)
        fun onMessageReceived(message: String)
        fun onError(error: String)
    }

    private var listener: ServerListener? = null

    fun initialize(listener: ServerListener) {
        this.listener = listener
    }

    fun start() {
        try {
            serverSocket = ServerSocket(SERVER_PORT)
            Log.d(TAG, "Mesh server started on port $SERVER_PORT")

            executor.execute {
                while (serverSocket != null && !serverSocket!!.isClosed) {
                    try {
                        val clientSocket = serverSocket!!.accept()
                        val clientHandler = ClientHandler(clientSocket)
                        clientHandlers.add(clientHandler)
                        executor.execute(clientHandler)

                        listener?.onClientConnected(clientSocket.inetAddress.hostAddress)
                    } catch (e: IOException) {
                        if (!serverSocket!!.isClosed) {
                            Log.e(TAG, "Error accepting client", e)
                            listener?.onError("Error accepting client: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error starting mesh server", e)
            listener?.onError("Error starting server: ${e.message}")
        }
    }

    fun stop() {
        clientHandlers.forEach { it.close() }
        clientHandlers.clear()

        try {
            serverSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error stopping server", e)
        }
    }

    fun broadcastMessage(message: String) {
        clientHandlers.forEach { handler ->
            try {
                handler.sendMessage(message)
            } catch (e: IOException) {
                Log.e(TAG, "Error broadcasting message", e)
                // Remove disconnected client
                clientHandlers.remove(handler)
            }
        }
    }

    private inner class ClientHandler(private val clientSocket: Socket) : Runnable {
        private val reader: BufferedReader
        private val writer: PrintWriter

        init {
            reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            writer = PrintWriter(OutputStreamWriter(clientSocket.getOutputStream()), true)
        }

        override fun run() {
            try {
                var message: String?
                while (reader.readLine().also { message = it } != null) {
                    message?.let {
                        Log.d(TAG, "Received message: $it")
                        listener?.onMessageReceived(it)
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error handling client", e)
            } finally {
                close()
            }
        }

        fun sendMessage(message: String) {
            writer.println(message)
        }

        fun close() {
            try {
                reader.close()
                writer.close()
                clientSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error closing client connection", e)
            }
        }
    }
}