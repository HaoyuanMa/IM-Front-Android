package com.mahaoyuan.realtime.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.models.Message

class StreamAdapter(var msgList: MutableList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class TextViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val streamMsg : TextView = view.findViewById(R.id.msg_content_stream)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_stream, parent, false)
        return TextViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = msgList[position]
        (holder as TextViewHolder).streamMsg.text = data
    }

    override fun getItemCount(): Int {
        return msgList.size
    }

}