package com.zrq.socketdemo.server

interface ClientCallback {
    //接收服务端消息
    fun receiveServerMsg(msg: String)

    //其他消息
    fun otherMsg(msg: String)
}