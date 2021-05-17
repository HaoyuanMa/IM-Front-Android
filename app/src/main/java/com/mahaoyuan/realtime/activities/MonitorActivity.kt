package com.mahaoyuan.realtime.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.UserInfo
import com.mahaoyuan.realtime.adapters.MessageAdapter
import com.mahaoyuan.realtime.adapters.StreamAdapter
import com.mahaoyuan.realtime.models.Message
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MonitorActivity : AppCompatActivity() {

    val Streamhandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: android.os.Message) {
            when(msg.what){
                3 ->  {
                    adapter?.notifyDataSetChanged()
                    val pos = dataStream.size - 1
                    recyclerView.scrollToPosition(pos)
                }
            }
            super.handleMessage(msg)
        }
    }

    lateinit var recyclerView : RecyclerView
    var adapter : StreamAdapter? = null
    val dataStream = mutableListOf<String>()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)
        setTitle("Monitor")

        val layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        adapter = StreamAdapter(dataStream)
        recyclerView.adapter = adapter

        UserInfo.connection.value?.stream(String::class.java,"DownloadStream",500)?.subscribe ({
            Log.i("mhy","receive: $it")
            dataStream.add(it)
            val msg = android.os.Message()
            msg.what = 3
            Streamhandler.sendMessage(msg)

        },{
          Log.i("mhy",it.message.toString())
        },{
            Log.i("mhy","stream end")
        })

    }
}