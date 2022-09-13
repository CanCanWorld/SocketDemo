package com.zrq.socketdemo

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.zrq.socketdemo.bean.Message
import com.zrq.socketdemo.databinding.ActivityMainBinding
import com.zrq.socketdemo.server.ClientCallback
import com.zrq.socketdemo.server.ServerCallback
import com.zrq.socketdemo.server.SocketClient
import com.zrq.socketdemo.server.SocketServer

class MainActivity : AppCompatActivity(), ServerCallback, ClientCallback {

    private lateinit var mBinding: ActivityMainBinding
    private var isServer = true
    private var isOpenServer = false
    private var isConnectSocket = false
    private val messages = ArrayList<Message>()
    private lateinit var adapter: MsgAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initData()
        initEvent()
    }

    private fun initData() {
        adapter = MsgAdapter(messages)
        mBinding.rvInfo.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initEvent() {
        mBinding.apply {
            tvIpAddress.text = "ip地址: ${getIp()}"
            etIpAddress.setText(getIp())
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rb_client -> {
                        isServer = false
                        linearLayoutClient.visibility = View.VISIBLE
                        linearLayoutServer.visibility = View.GONE
                        etSend.hint = "发送给服务端"
                    }
                    else -> {
                        isServer = true
                        linearLayoutClient.visibility = View.GONE
                        linearLayoutServer.visibility = View.VISIBLE
                        etSend.hint = "发送给客户端"
                    }
                }
            }
            btnOpenServer.setOnClickListener {
                if (isOpenServer) {
                    SocketServer.stopServer()
                    updateList(1, "${getIp()}关闭服务")
                    btnOpenServer.text = "开启服务"
                } else {
                    SocketServer.startServer(this@MainActivity)
                    updateList(1, "${getIp()}开启服务")
                    btnOpenServer.text = "关闭服务"
                }
                isOpenServer = !isOpenServer
            }
            btnConnectServer.setOnClickListener {
                val ip = etIpAddress.text.toString()
                if (ip.isEmpty()) {
                    Toast.makeText(this@MainActivity, "请输入ip地址", Toast.LENGTH_SHORT).show()
                }
                if (isConnectSocket) {
                    SocketClient.closeConnect()
                    btnConnectServer.text = "连接服务"
                    updateList(2, "$ip 关闭连接")
                } else {
                    SocketClient.connectServer(ip, this@MainActivity)
                    btnConnectServer.text = "关闭连接"
                    updateList(2, "$ip 连接服务")
                }
                isConnectSocket = !isConnectSocket
            }
            btnSend.setOnClickListener {
                val msg = etSend.text.toString()
                if (msg.isEmpty()) {
                    Toast.makeText(this@MainActivity, "请输入要发送的消息", Toast.LENGTH_SHORT).show()
                }
                if (isOpenServer && isConnectSocket) {
                    if (isServer) SocketServer.sendToClient(msg) else SocketClient.sendToServer(msg)
                } else {
                    Toast.makeText(this@MainActivity, "当前未开启服务或未连接服务", Toast.LENGTH_SHORT).show()
                }
                etSend.setText("")
            }
        }
    }

    override fun receiveServerMsg(msg: String) {
        updateList(1, msg)
    }

    override fun receiveClientMsg(success: Boolean, msg: String) {
        updateList(2, msg)
    }

    override fun otherMsg(msg: String) {
        Log.d(TAG, "otherMsg: $msg")
    }

    companion object {
        const val TAG = "MainActivity"
    }

    private fun getIp() =
        intToIp((applicationContext.getSystemService(WIFI_SERVICE) as WifiManager).connectionInfo.ipAddress)

    private fun intToIp(ip: Int) =
        "${(ip and 0xFF)}.${(ip shr 8 and 0xFF)}.${(ip shr 16 and 0xFF)}.${(ip shr 24 and 0xFF)}"

    private fun updateList(type: Int, msg: String) {
        messages.add(Message(type, msg))
        runOnUiThread {
            (if (messages.size == 0) 0 else messages.size - 1).apply {
                adapter.notifyItemChanged(this)
                mBinding.rvInfo.smoothScrollToPosition(this)
            }
        }
    }
}