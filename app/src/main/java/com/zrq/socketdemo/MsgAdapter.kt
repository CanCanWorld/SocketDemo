package com.zrq.socketdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.zrq.socketdemo.bean.Message
import com.zrq.socketdemo.databinding.ItemLeftMsgBinding
import com.zrq.socketdemo.databinding.ItemRightMsgBinding

class MsgAdapter(private val messages: ArrayList<Message>) :
    RecyclerView.Adapter<MsgAdapter.InnerHolder>() {

    class InnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val binding: ViewBinding = when (viewType) {
            1 -> {
                ItemLeftMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            }
            else -> {
                ItemRightMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            }
        }
        return InnerHolder(binding.root)
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        val message = messages[position]
        when (message.type) {
            1 -> {
                holder.itemView.findViewById<TextView>(R.id.tv_left_msg).text = message.msg
            }
            else -> {
                holder.itemView.findViewById<TextView>(R.id.tv_right_msg).text = message.msg
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return messages[position].type
    }
}