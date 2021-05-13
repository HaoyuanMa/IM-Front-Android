package com.mahaoyuan.realtime.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.UserInfo

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chatButton : Button = findViewById(R.id.btn_chat)
        val broadcastHostButton : Button = findViewById(R.id.btn_broadcast_host)
        val broadcastReceiveButton : Button = findViewById(R.id.btn_broadcast_receive)
        val chatRoomButton : Button = findViewById(R.id.btn_chatroom)
        val monitorButton : Button = findViewById(R.id.btn_monitor)

        chatButton.setOnClickListener {
            UserInfo.BuildConnection()
            UserInfo.StartConnection()
            Log.i("mhy","conn start")
            UserInfo.Bind()
            UserInfo.mode.value = "chat"
            UserInfo.SetOnline("chat")
            val intent = Intent(this@MainActivity, UsersListActivity::class.java)
            startActivity(intent)
        }
        broadcastHostButton.setOnClickListener {
            UserInfo.BuildConnection()
            UserInfo.StartConnection()
            Log.i("mhy","conn start")
            UserInfo.Bind()
            UserInfo.mode.value = "broadcast"
            UserInfo.SetOnline("broadcast")
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            startActivity(intent)
        }
        broadcastReceiveButton.setOnClickListener {
            UserInfo.BuildConnection()
            UserInfo.StartConnection()
            Log.i("mhy","conn start")
            UserInfo.Bind()
            UserInfo.mode.value = "broadcast"
            UserInfo.SetOnline("broadcast")
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            startActivity(intent)
        }
        chatRoomButton.setOnClickListener {
            UserInfo.BuildConnection()
            UserInfo.StartConnection()
            Log.i("mhy","conn start")
            UserInfo.Bind()
            UserInfo.mode.value = "chatroom"
            UserInfo.SetOnline("chatroom")
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            startActivity(intent)
        }
        monitorButton.setOnClickListener {

        }
    }
}