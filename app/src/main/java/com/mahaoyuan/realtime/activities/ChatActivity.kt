package com.mahaoyuan.realtime.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.UserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

//        UserInfo.BuildConnection()
//        UserInfo.StartConnection()
//        Log.i("mhy","conn start")
//        UserInfo.connection.value?.connectionState?.let { Log.i("mhy", it.name) }

//        runBlocking {
//            UserInfo.BuildConnection()
//            val start = launch { UserInfo.StartConnection() }
//            start.join()
//            Log.i("mhy","conn start")
//            UserInfo.connection.value?.connectionState?.let { Log.i("mhy", it.name) }
//        }


        val listView = findViewById<ListView>(R.id.chat_users)
        val adapter = UserInfo.chatUsers.value?.let {
            ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                it.toList())
        }

        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, pos, id ->
            Toast.makeText(this,"click",Toast.LENGTH_SHORT).show()
            val item = UserInfo.chatUsers.value?.get(pos)

            if (item != null) {
                UserInfo.chatUsers.value!!.add(item)
                UserInfo.usersCount.value = UserInfo.usersCount.value?.plus(1)
                Log.i("mhy",item)
            }
        }

        UserInfo.usersCount.observe(this, Observer { users ->
            Log.i("mhy","changed")
            val newAdapter = UserInfo.chatUsers.value?.let {
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                    it.toList())
            }
            listView.adapter = newAdapter
        })


    }
}