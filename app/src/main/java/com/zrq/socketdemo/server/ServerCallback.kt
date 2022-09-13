package com.zrq.socketdemo.server

interface ServerCallback {
    //接收客户端信息
    fun receiveClientMsg(success: Boolean, msg: String)

    //其他信息
    fun otherMsg(msg: String)
}