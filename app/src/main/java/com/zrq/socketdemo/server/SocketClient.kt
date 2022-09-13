package com.zrq.socketdemo.server

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket

object SocketClient {
    private const val TAG = "SocketClient"

    private var socket: Socket? = null

    private var outputStream: OutputStream? = null

    private var inputStreamReader: InputStreamReader? = null

    private lateinit var mCallback: ClientCallback

    private const val SOCKET_PORT = 1111

    //连接服务
    fun connectServer(ipAddress: String, callback: ClientCallback) {
        mCallback = callback
        Thread {
            try {
                socket = Socket(ipAddress, SOCKET_PORT)
                ClientThread(socket!!, mCallback).start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    //关闭连接
    fun closeConnect() {
        inputStreamReader?.close()
        outputStream?.close()
        socket?.apply {
            shutdownInput()
            shutdownOutput()
            close()
        }
        Log.d(TAG, "closeConnect: ")
    }

    //发送数据至服务器
    fun sendToServer(msg: String) {
        Thread {
            if (socket != null) {
                if (socket!!.isClosed) {
                    Log.e(TAG, "sendToServer: Socket is closed")
                    return@Thread
                }
                outputStream = socket?.getOutputStream()
                try {
                    outputStream?.write(msg.toByteArray())
                    outputStream?.flush()
                    mCallback.otherMsg("toServer: $msg")
                    Log.d(TAG, "sendToServer: success")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e(TAG, "sendToServer: failure")
                }
            }
        }.start()
    }

    class ClientThread(private val socket: Socket, private val callback: ClientCallback) :
        Thread() {
        override fun run() {
            val inputStream: InputStream
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
                        callback.receiveServerMsg(receiveStr)
                        receiveStr = ""
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                e.message?.let { Log.e("socket error", it) }
                callback.receiveServerMsg("")
            }
        }
    }
}