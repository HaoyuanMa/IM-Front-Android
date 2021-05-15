package com.mahaoyuan.realtime.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI


class ChatActivity : AppCompatActivity() {

    var adapter : MessageAdapter? = null

    val fromAlbum = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setTitle(when (UserInfo.mode.value) {
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
            else -> MessageAdapter(mutableListOf(Message("chat", content = "null")))
        }
        recyclerView.adapter = adapter

        UserInfo.recordCount.observe(this, Observer { record ->
            Log.i("mhy", "records changed")
            var pos = when (UserInfo.mode.value) {
                "chat" -> UserInfo.chatRecords.value?.size?.minus(1)!!
                "broadcast" -> UserInfo.broadcastRecords.value?.size?.minus(1)!!
                "chatroom" -> UserInfo.chatRoomRecords.value?.size?.minus(1)!!
                else -> 0
            }
            adapter!!.notifyDataSetChanged()
            recyclerView.scrollToPosition(pos)
        })

        val sendMsgBtn = findViewById<Button>(R.id.btn_send)
        sendMsgBtn.setOnClickListener {
            sendText()
        }

        val sendImageBtn = findViewById<Button>(R.id.btn_image)
        sendImageBtn.setOnClickListener {
            sendImage()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        //todo: stop broadcast conn
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        val bitmap = contentResolver.openFileDescriptor(uri, "r")?.use {
                            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
                        }
                        val imageByteArray = bitmap?.let { compressImage(it) }
                        val msgContent = "data:image/;base64," + Base64.encodeToString(imageByteArray, Base64.DEFAULT)
                        val msgFrom = UserInfo.userEmail.value.toString()
                        val msgTo = when (UserInfo.mode.value) {
                            "chat" -> mutableListOf(UserInfo.chatTo.value.toString())
                            "broadcast" -> UserInfo.broadcastUsers.value
                            "chatroom" -> UserInfo.chatRoomUsers.value
                            else -> mutableListOf()
                        }
                        val msg = Message(UserInfo.mode.value.toString(), msgFrom, msgTo!!, "image", msgContent, 0)
                        UserInfo.SendMessage(msg)
                    }
                }
            }
        }
    }


    fun sendText(){
        val token = window.decorView.windowToken
        val inputMethodManager :InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)

        val textView = findViewById<TextView>(R.id.text_msg)
        val msgFrom = UserInfo.userEmail.value.toString()
        val msgTo = when(UserInfo.mode.value){
            "chat" -> mutableListOf(UserInfo.chatTo.value.toString())
            "broadcast" -> UserInfo.broadcastUsers.value
            "chatroom" -> UserInfo.chatRoomUsers.value
            else -> mutableListOf()
        }
        val msgContent = textView.text.toString()
        val msg = Message(UserInfo.mode.value.toString(), msgFrom, msgTo!!, "text", msgContent, 0)
        UserInfo.SendMessage(msg)
        textView.text = ""
    }

    fun sendImage(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, fromAlbum)
    }

    fun compressImage(image: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var options = 90
        while (baos.toByteArray().size / 1024 > 128) {
            baos.reset()
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)
            options -= 10
        }

        return baos.toByteArray()
    }

}