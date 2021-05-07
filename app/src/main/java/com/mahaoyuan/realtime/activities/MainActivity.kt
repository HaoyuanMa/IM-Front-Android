package com.mahaoyuan.realtime.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.mahaoyuan.realtime.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chatButton : Button = findViewById(R.id.btn_chat)
        val broadcastHostButton : Button = findViewById(R.id.btn_broadcast_host)
        val broadcastReceiveButton : Button = findViewById(R.id.btn_broadcast_receive)
        val chatRoomButton : Button = findViewById(R.id.btn_chatroom)
        val monitorButton : Button = findViewById(R.id.btn_monitor)

        chatButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            startActivity(intent)
        }
        broadcastHostButton.setOnClickListener {

        }
        broadcastReceiveButton.setOnClickListener {

        }
        chatRoomButton.setOnClickListener {

        }
        monitorButton.setOnClickListener {

        }
    }
}