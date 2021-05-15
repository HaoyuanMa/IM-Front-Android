package com.mahaoyuan.realtime.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.UserInfo
import com.mahaoyuan.realtime.adapters.MessageAdapter
import com.mahaoyuan.realtime.models.Message

class ChatActivity : AppCompatActivity() {

    var adapter : MessageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setTitle(when(UserInfo.mode.value){
            "chat" -> ("Chat to " + UserInfo.chatTo.value)
            "broadcast" -> (UserInfo.broadcastHost.value)
            "chatroom" -> "ChatRoom"
            else -> "chat"
        })

        val layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        adapter = when(UserInfo.mode.value){
            "chat" -> MessageAdapter(UserInfo.chatRecords.value!!)
            "broadcast" -> MessageAdapter(UserInfo.broadcastRecords.value!!)
            "chatroom" -> MessageAdapter(UserInfo.chatRoomRecords.value!!)
            else -> MessageAdapter(mutableListOf(Message("chat",content = "null")))
        }
        recyclerView.adapter = adapter

        UserInfo.recordCount.observe(this, Observer { record ->
            Log.i("mhy","records changed")
            var pos = when(UserInfo.mode.value){
                "chat" -> UserInfo.chatRecords.value?.size?.minus(1)!!
                "broadcast" -> UserInfo.broadcastRecords.value?.size?.minus(1)!!
                "chatroom" -> UserInfo.chatRoomRecords.value?.size?.minus(1)!!
                else -> 0
            }
            adapter!!.notifyDataSetChanged()
            recyclerView.scrollToPosition(pos)
        })

        val sendMsg = findViewById<Button>(R.id.btn_send)
        sendMsg.setOnClickListener {
            sendText()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        //todo: stop broadcast conn
    }


    fun sendText(){
        val token = window.decorView.windowToken
        val inputMethodManager :InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(token,InputMethodManager.HIDE_NOT_ALWAYS)

        val textView = findViewById<TextView>(R.id.text_msg)
        val msgFrom = UserInfo.userEmail.value.toString()
        val msgTo = when(UserInfo.mode.value){
            "chat" -> mutableListOf(UserInfo.chatTo.value.toString())
            "broadcast" -> UserInfo.broadcastUsers.value
            "chatroom" -> UserInfo.chatRoomUsers.value
            else -> mutableListOf()
        }
        val msgContent = textView.text.toString()
        val msg = Message("chat",msgFrom, msgTo!!,"text",msgContent,0)
        UserInfo.SendMessage(msg)
        textView.text = ""
    }

}