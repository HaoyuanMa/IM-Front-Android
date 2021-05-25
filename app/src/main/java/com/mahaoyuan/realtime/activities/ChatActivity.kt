package com.mahaoyuan.realtime.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.UserInfo
import com.mahaoyuan.realtime.adapters.MessageAdapter
import com.mahaoyuan.realtime.models.BinData
import com.mahaoyuan.realtime.models.Message
import io.reactivex.subjects.ReplaySubject
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread


class ChatActivity : AppCompatActivity() {

    var adapter : MessageAdapter? = null
    private val imageFile = 1
    private val commonFile = 2

    inner class FileMetaData(
            var name: String = "",
            var size: Long = 0
    )

    private val uploadHandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: android.os.Message) {
            when(msg.what){
                2 ->  {
                    adapter!!.notifyDataSetChanged()
                }
            }
            super.handleMessage(msg)
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        title = when (UserInfo.mode.value) {
            "chat" -> ("Chat to " + UserInfo.chatTo.value)
            "broadcast" -> "Broadcast"
            "chatroom" -> "ChatRoom"
            else -> "chat"
        }

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

        UserInfo.recordCount.observe(this, Observer {
            Log.i("mhy", "records changed")
            val pos = when (UserInfo.mode.value) {
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

        val sendFileBtn = findViewById<Button>(R.id.btn_file)
        sendFileBtn.setOnClickListener {
            sendFile()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        if(UserInfo.mode.value != "chat"){
            UserInfo.stopConnection()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            imageFile -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        val bitmap = contentResolver.openFileDescriptor(uri, "r")?.use {
                            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
                        }
                        val imageByteArray = bitmap?.let { compressImage(it) }
                        val msgContent = "data:image/;base64," +
                                Base64.encodeToString(imageByteArray, Base64.DEFAULT)
                        val msgFrom = UserInfo.userEmail.value.toString()
                        val msgTo = when (UserInfo.mode.value) {
                            "chat" -> mutableListOf(UserInfo.chatTo.value.toString())
                            "broadcast" -> UserInfo.broadcastUsers.value
                            "chatroom" -> UserInfo.chatRoomUsers.value
                            else -> mutableListOf()
                        }
                        val msg = Message(
                                type = UserInfo.mode.value.toString(),
                                from = msgFrom,
                                to = msgTo!!,
                                contentType = "image",
                                content = msgContent,
                                fileSize = 0
                        )
                        UserInfo.sendMessage(msg)
                    }
                }
            }
            commonFile -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        Log.i("mhy", uri.toString())
                        thread {
                            val stream = ReplaySubject.create<BinData>(40)
                            UserInfo.connection.value?.send("UploadFile", stream)
                            val fileStream = contentResolver.openInputStream(uri)
                            val bytes = ByteArray(128*1024) { 0.toByte() }
                            val fileInfo = getFileMetaData(uri)
                            val msgTo = when (UserInfo.mode.value) {
                                "chat" -> mutableListOf(UserInfo.chatTo.value.toString())
                                "broadcast" -> UserInfo.broadcastUsers.value
                                "chatroom" -> UserInfo.chatRoomUsers.value
                                else -> mutableListOf()
                            }
                            val msg = Message(UserInfo.mode.value.toString(),UserInfo.userEmail.value.toString(),msgTo!!,"file",fileInfo.name,fileInfo.size)
                            var curMsgPos = 0
                            when(UserInfo.mode.value){
                                "chat" -> {
                                    UserInfo.chatRecords.value?.add(msg)
                                    UserInfo.recordCount.postValue(UserInfo.recordCount.value?.plus(1))
                                    curMsgPos = (UserInfo.chatRecords.value?.size!! - 1)
                                }
                                "broadcast" -> {
                                    UserInfo.broadcastRecords.value?.add(msg)
                                    UserInfo.recordCount.postValue(UserInfo.recordCount.value?.plus(1))
                                    curMsgPos = (UserInfo.broadcastRecords.value?.size!! - 1)
                                }
                                "chatroom" -> {
                                    UserInfo.chatRoomRecords.value?.add(msg)
                                    UserInfo.recordCount.postValue(UserInfo.recordCount.value?.plus(1))
                                    curMsgPos = (UserInfo.chatRoomRecords.value?.size!! - 1)
                                }
                                else -> {}
                            }
                            var count = 0
                            var order:Long = 0
                            var binData : BinData
                            while (count >= 0) {
                                if (fileStream != null) {
                                    count = fileStream.read(bytes)
                                }
                                if (count < 0) break
                                binData = BinData(
                                        Name = fileInfo.name,
                                        From = UserInfo.userEmail.value.toString(),
                                        Data = "data:file/;base64," +
                                                Base64.encodeToString(bytes.sliceArray(0 until count), Base64.DEFAULT),
                                        Order = order
                                )
                                stream.onNext(binData)
                                order += 1
                            }
                            fileStream?.close()
                            stream.onComplete()
                            Log.i("mhy","send: file completed")
                            //发送消息通知接收者
                            UserInfo.sendMessage(msg)
                            when(UserInfo.mode.value){
                                "chat" -> UserInfo.chatRecords.value?.get(curMsgPos)?.fileSize = 0
                                "broadcast" -> UserInfo.broadcastRecords.value?.get(curMsgPos)?.fileSize = 0
                                "chatroom" -> UserInfo.chatRoomRecords.value?.get(curMsgPos)?.fileSize = 0
                                else -> {}
                            }
                            //发送Message通知UI线程更新View显示
                            val message = android.os.Message()
                            message.what = 2
                            uploadHandler.sendMessage(message)
                        }
                    }
                }
            }
        }
    }


    private fun sendText(){
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
        UserInfo.sendMessage(msg)
        textView.text = ""
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun sendImage(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, imageFile)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun sendFile(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, commonFile)
    }

    private fun compressImage(image: Bitmap): ByteArray {
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



    private fun getFileMetaData(uri: Uri):FileMetaData {
        val contentResolver = applicationContext.contentResolver
        val cursor: Cursor? = contentResolver.query(
                uri, null, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName: String =
                        it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Log.i("mhy", "Display Name: $displayName")
                val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                val size: String = if (!it.isNull(sizeIndex)) {
                    it.getString(sizeIndex)
                } else {
                    "Unknown"
                }
                Log.i("mhy", "Size: $size")
                return FileMetaData(displayName, size.toLong())
            }
        }
        return FileMetaData("", 0)
    }
}