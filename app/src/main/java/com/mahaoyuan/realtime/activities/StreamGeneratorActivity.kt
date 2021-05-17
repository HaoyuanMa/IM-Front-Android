package com.mahaoyuan.realtime.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.UserInfo
import com.mahaoyuan.realtime.adapters.StreamAdapter
import io.reactivex.subjects.ReplaySubject
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class StreamGeneratorActivity : AppCompatActivity() {

    private val showhandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                4 ->  {
                    adapter?.notifyDataSetChanged()
                    val pos = dataStream.size - 1
                    recyclerView.scrollToPosition(pos)
                }
            }
            super.handleMessage(msg)
        }
    }

    private lateinit var recyclerView : RecyclerView
    private var adapter : StreamAdapter? = null
    private val dataStream = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream_generator)
        title = "StreamGenerator"

        val layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.stream_log)
        recyclerView.layoutManager = layoutManager
        adapter = StreamAdapter(dataStream)
        recyclerView.adapter = adapter


        val dataCountView : EditText = findViewById(R.id.stream_count)
        val dataSpeedView : EditText = findViewById(R.id.stream_speed)
        val generateBtn : Button = findViewById(R.id.stream_generate)
        Log.i("mhy","before Click ${generateBtn.visibility}")
        generateBtn.setOnClickListener {
            thread {
                Log.i("mhy","clicked ${it.visibility}")
                Log.i("mhy","generate")
                val dataCount : Int = if(dataCountView.text.toString().isEmpty()) 0 else dataCountView.text.toString().toInt()
                val dataSpeed : Long = if(dataSpeedView.text.toString().isEmpty()) 0 else dataSpeedView.text.toString().toLong()
                Log.i("mhy","count: $dataCount, speed: $dataSpeed")
                val stream = ReplaySubject.create<String>(40)
                UserInfo.connection.value?.send("UploadStream", stream)
                for (index in 0..dataCount){
                    Log.i("mhy","send stream: $index")
                    stream.onNext(index.toString())
                    dataStream.add("send: $index")
                    val msg = Message()
                    msg.what = 4
                    showhandler.sendMessage(msg)
                    sleep(dataSpeed)
                }
                stream.onComplete()
            }
        }

    }
}