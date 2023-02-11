package br.com.mrocigno.dreaming

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket

class DebugServer : Runnable {

    private lateinit var serverSocket: ServerSocket
    private lateinit var socket: Socket
    private lateinit var dataInputStream: DataInputStream
    private lateinit var dataOutputStream: DataOutputStream
    private val thread = Thread(this).apply {
        priority = Thread.NORM_PRIORITY
        start()
    }

    override fun run() {
        serverSocket = runCatching { ServerSocket(1234) }
            .onFailure(::stopThread)
            .getOrNull() ?: return

        // Waiting for connection
        socket = runCatching { serverSocket.accept() }
            .onFailure(::stopThread)
            .getOrNull() ?: return

        dataInputStream = runCatching { DataInputStream(BufferedInputStream(socket.getInputStream())) }
            .onFailure(::stopThread)
            .getOrNull() ?: return

        dataOutputStream = runCatching { DataOutputStream(BufferedOutputStream(socket.getOutputStream())) }
            .onFailure(::stopThread)
            .getOrNull() ?: return
    }

    private fun stopThread(e: Throwable) {
        e.printStackTrace()
        thread.interrupt()
    }
}