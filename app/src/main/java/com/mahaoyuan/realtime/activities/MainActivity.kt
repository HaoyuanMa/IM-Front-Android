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
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {
    inner class ConnenctionObserver(mode: String, intent: Intent) : CompletableObserver {
        private var _mode = mode
        private var _intent : Intent? = intent

        override fun onSubscribe(d: Disposable) {
            Log.i("mhy","subscribe conn")
        }

        override fun onComplete() {
            UserInfo.mode.postValue(_mode)
            UserInfo.SetOnline(_mode)
            startActivity(_intent)
        }

        override fun onError(e: Throwable) {
            e.message?.let { Log.i("mhy", it) }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chatButton : Button = findViewById(R.id.btn_chat)
        val broadcastHostButton : Button = findViewById(R.id.btn_broadcast_host)
        val broadcastReceiveButton : Button = findViewById(R.id.btn_broadcast_receive)
        val chatRoomButton : Button = findViewById(R.id.btn_chatroom)
        val monitorButton : Button = findViewById(R.id.btn_monitor)
        val streamGeneratorButton : Button = findViewById(R.id.btn_stream_generator)

        chatButton.setOnClickListener {
            val intent = Intent(this@MainActivity, UsersListActivity::class.java)
            startMode("chat",intent)
        }

        broadcastHostButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            startMode("broadcast",intent)
        }
        broadcastReceiveButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
           startMode("broadcast",intent)
        }
        chatRoomButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            startMode("chatroom",intent)
        }
        monitorButton.setOnClickListener {
            val intent = Intent(this@MainActivity, MonitorActivity::class.java)
            startMode("stream",intent)
        }
        streamGeneratorButton.setOnClickListener {
            val intent = Intent(this@MainActivity, StreamGeneratorActivity::class.java)
            startMode("stream",intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun startMode(mode: String,intent: Intent){
        UserInfo.BuildConnection()
        UserInfo.Bind()
        val observer : CompletableObserver = ConnenctionObserver(mode,intent)
        UserInfo.StartConnection()?.subscribe(observer)
        Log.i("mhy","conn start")
    }
}