package com.zrq.socketdemo.server

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

object SocketServer {

    private const val TAG = "SocketServer"

    private const val SOCKET_PORT = 1111

    private var socket: Socket? = null

    private var serverSocket: ServerSocket? = null

    private lateinit var mCallback: ServerCallback

    private lateinit var outputStream: OutputStream

    private var result = true

    //开启服务
    fun startServer(callback: ServerCallback): Boolean {
        mCallback = callback
        Thread {
            try {
                serverSocket = ServerSocket(SOCKET_PORT)
                while (result) {
                    Log.d(TAG, "startServer: ")
                    socket = serverSocket?.accept()
                    mCallback.otherMsg("${socket?.inetAddress} to connected")
                    if (socket != null) {
                        ServerThread(socket!!, mCallback).start()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                result = false
            }
        }.start()
        return result
    }

    //关闭服务
    fun stopServer() {
        socket?.apply {
            shutdownInput()
            shutdownOutput()
            close()
        }
        serverSocket?.close()
    }

    fun sendToClient(msg: String) {
        Thread {
            if (socket != null) {
                if (socket!!.isClosed) {
                    Log.e(TAG, "sendToClient: Socket is closed")
                    return@Thread
                }
                outputStream = socket!!.getOutputStream()
                try {
                    outputStream.write(msg.toByteArray())
                    outputStream.flush()
                    mCallback.otherMsg("toClient: $msg")
                    Log.d(TAG, "sendToClient: success")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e(TAG, "sendToClient: failure")
                }
            }
        }.start()
    }

    class ServerThread(private val socket: Socket, private val callback: ServerCallback) :
        Thread() {
        override fun run() {
            val inputStream: InputStream?
            try {
                inputStream = socket.getInputStream()
                val buffer = ByteArray(1024)
                var len: Int
                var receiveStr = ""
                if (inputStream.available() == 0) {
                    Log.e(TAG, "inputStream.available() == 0")
                }
                while (inputStream.read(buffer).also { len = it } != -1) {
                    receiveStr += String(buffer, 0, len, Charsets.UTF_8)
                    if (len < 1024) {
                        callback.receiveClientMsg(true, receiveStr)
                        receiveStr = ""
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                e.message?.let { Log.e("socket error", it) }
                callback.receiveClientMsg(false, "")
            }
        }
    }
}