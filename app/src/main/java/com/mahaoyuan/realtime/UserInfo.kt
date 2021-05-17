package com.mahaoyuan.realtime

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.Single
import com.mahaoyuan.realtime.models.Message
import io.reactivex.Completable

object UserInfo {
    const val host = "http://182.92.183.106:12165"
    val mode = MutableLiveData<String>()
    val connection = MutableLiveData<HubConnection?>()
    val userEmail = MutableLiveData<String>()
    val token = MutableLiveData<String>()
    val chatTo = MutableLiveData<String>()
    private val broadcastHost = MutableLiveData<String>()
    val chatUsers = MutableLiveData<MutableList<String>>()
    val broadcastUsers = MutableLiveData<MutableList<String>>()
    val chatRoomUsers = MutableLiveData<MutableList<String>>()
    val usersCount = MutableLiveData<Int>()
    val chatRecords = MutableLiveData<MutableList<Message>>()
    val broadcastRecords = MutableLiveData<MutableList<Message>>()
    val chatRoomRecords = MutableLiveData<MutableList<Message>>()
    val recordCount = MutableLiveData<Int>()

    init {
        mode.value = ""
        connection.value = null
        userEmail.value = ""
        token.value = ""
        chatTo.value = ""
        broadcastHost.value = ""
        chatUsers.value = mutableListOf()
        broadcastUsers.value = mutableListOf()
        chatRoomUsers.value = mutableListOf()
        usersCount.value = 0
        chatRecords.value = mutableListOf()
        broadcastRecords.value = mutableListOf()
        chatRoomRecords.value = mutableListOf()
        recordCount.value = 0
        Log.i("mhy", "init UserInfo")
        Log.i("mhy",Char.SIZE_BYTES.toString())

    }

    fun buildConnection(){
        connection.value = HubConnectionBuilder.create("$host/Hubs/MessageHub")
            .withAccessTokenProvider(Single.defer { Single.just(token.value) }).build()
    }

    fun startConnection() : Completable? {
        return connection.value?.start()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun bind(){
        val conn = connection.value
        if (conn != null) {
            conn.on("RemoveUser", { user: String ->
                chatUsers.value?.removeIf { t :String -> t == user  }
                broadcastUsers.value?.removeIf { t:String -> t == user }
                chatRoomUsers.value?.removeIf { t:String -> t == user }
                usersCount.postValue(usersCount.value?.minus(1))
                Log.i("mhy","remove user")
            },String::class.java)

            conn.on("GetChatUsers",{users : MutableList<String> ->
                users.forEach {
                    chatUsers.value?.add(it)
                    usersCount.postValue(usersCount.value?.plus(1))
                }
                Log.i("mhy","get chat users")
            }, mutableListOf<String>()::class.java)

            conn.on("GetBroadcastUsers",{users : MutableList<String> ->
                users.forEach {
                    broadcastUsers.value?.add(it)
                    usersCount.postValue(usersCount.value?.plus(1))
                }
                Log.i("mhy","get broadcast users")
            }, mutableListOf<String>()::class.java)

            conn.on("GetChatRoomUsers",{users : MutableList<String> ->
                users.forEach {
                    chatRoomUsers.value?.add(it)
                    usersCount.postValue(usersCount.value?.plus(1))
                }
                Log.i("mhy","get chatroom users")
            }, mutableListOf<String>()::class.java)

            conn.on("ReceiveMessage",{msg : Message ->
                Log.i("mhy",msg.content)
                when(msg.type){
                    "chat" ->{
                        chatRecords.value?.add(msg)
                        recordCount.postValue(recordCount.value?.plus(1))
                    }
                    "broadcast" -> if(msg.contentType != "file" || msg.from != userEmail.value){
                        broadcastRecords.value?.add(msg)
                        recordCount.postValue(recordCount.value?.plus(1))
                    }
                    "chatroom" -> if(msg.contentType != "file" || msg.from != userEmail.value){
                        chatRoomRecords.value?.add(msg)
                        recordCount.postValue(recordCount.value?.plus(1))
                    }
                    else -> return@on
                }
            },Message::class.java)

        }

    }

    fun setOnline(type :String){
        connection.value?.send("SetOnline",type)
        Log.i("mhy","setOnline")
    }

    fun stopConnenction(){
        chatUsers.value?.clear()
        broadcastUsers.value?.clear()
        chatRoomUsers.value?.clear()
        chatRecords.value?.clear()
        broadcastRecords.value?.clear()
        chatRoomRecords.value?.clear()
        usersCount.value = 0
        recordCount.value = 0
        connection.value?.stop()
    }

    fun sendMessage(msg: Message){
        if (msg.contentType != "file"){
            chatRecords.value?.add(msg)
            recordCount.postValue(recordCount.value?.plus(1))
        }
        connection.value?.send("SendMessage",msg)
        Log.i("mhy","send: " + msg.contentType)
    }
}