package com.mahaoyuan.realtime.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.UserInfo
import com.mahaoyuan.realtime.models.Message
import com.mahaoyuan.realtime.util.FullScreen

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val sendMsg = findViewById<Button>(R.id.btn_send)
        sendMsg.setOnClickListener {
            sendText()
        }

    }


    fun sendText(){
        val textView = findViewById<TextView>(R.id.text_msg)
        val msgFrom = UserInfo.userEmail.value.toString()
        val msgTo = mutableListOf(UserInfo.chatTo.value.toString())
        val msgContent = textView.text.toString()
        val msg = Message("chat",msgFrom, msgTo,"text",msgContent,0)
        UserInfo.SendMessage(msg)
        textView.text = ""
    }
}