package com.mahaoyuan.realtime.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.UserInfo

class UsersListActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userslist)

        UserInfo.BuildConnection()
        UserInfo.StartConnection()
        Log.i("mhy","conn start")
        UserInfo.Bind()
        UserInfo.mode.value = "chat"
        UserInfo.SetOnline("chat")

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
            //Toast.makeText(this,"click",Toast.LENGTH_SHORT).show()
            //todo: open chat acti
            val item = UserInfo.chatUsers.value?.get(pos)

            if (item != null) {
               UserInfo.chatTo.value = item
                val intent = Intent(this@UsersListActivity, ChatActivity::class.java)
                startActivity(intent)
            }
            //todo
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

    override fun onDestroy() {
        super.onDestroy()

        UserInfo.StopConnenction()
    }
}