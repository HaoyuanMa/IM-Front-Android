package com.mahaoyuan.realtime.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.UserInfo
import com.mahaoyuan.realtime.models.Message
import java.lang.IllegalArgumentException

class MessageAdapter(var msgList: MutableList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class TextViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val msgFrom : TextView = view.findViewById(R.id.msg_from_text)
        val textMsg : TextView = view.findViewById(R.id.msg_content_text)
    }

    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }

    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position]
        return if (msg.type == "chat"){
            if ((msg.from == UserInfo.userEmail.value && msg.to[0] == UserInfo.chatTo.value) || (msg.from == UserInfo.chatTo.value  && msg.to[0] == UserInfo.userEmail.value)){
                when(msg.contentType){
                    "text" -> 1
                    "image" -> 2
                    "file" -> 3
                    else -> 0
                }
            }else{
                0
            }
        }else{
            when(msg.contentType){
                "text" -> 1
                "image" -> 2
                "file" -> 3
                else -> 0
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 1) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.message_text,parent,false)
            TextViewHolder(view)
        }
        else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.message_empty,parent,false)
            EmptyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = msgList[position]
        when(holder){
            is TextViewHolder -> {
                holder.textMsg.text = ("   " + msg.content)
                holder.msgFrom.text = (" " + msg.from)
            }
            is EmptyViewHolder -> {

            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount() = msgList.size

}