package com.mahaoyuan.realtime

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.Single
import com.mahaoyuan.realtime.models.Message
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


object UserInfo {
    val mode = MutableLiveData<String>()
    val connection = MutableLiveData<HubConnection?>()
    val userEmail = MutableLiveData<String>()
    val token = MutableLiveData<String>()
    val chatTo = MutableLiveData<String>()
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
        chatUsers.value = mutableListOf()
        broadcastUsers.value = mutableListOf()
        chatRoomUsers.value = mutableListOf()
        usersCount.value = 0
        chatRecords.value = mutableListOf(Message("chat","mhy", mutableListOf("liyi"),"text","hello",0),Message("chat","mhy", mutableListOf("liyi"),"text","hello",0))
        broadcastRecords.value = mutableListOf()
        chatRoomRecords.value = mutableListOf()
        Log.i("mhy", "init UserInfo")

    }

    fun BuildConnection(){
        connection.value = HubConnectionBuilder.create("http://10.0.2.2:12165/Hubs/MessageHub")
            .withAccessTokenProvider(Single.defer({ Single.just(token.value) })).build()
    }

    fun StartConnection(){
        val start = connection.value?.start()
        if (start != null) {
            start.blockingGet()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun Bind(){
        val conn = connection.value
        if (conn != null) {
            conn.on("RemoveUser", { user: String ->
                chatUsers.value?.removeIf { t :String -> t == user  }
                broadcastUsers.value?.removeIf { t:String -> t == user }
                chatRoomUsers.value?.removeIf { t:String -> t == user }
                usersCount.postValue(usersCount.value?.minus(1))
                Log.i("mhy","remove user")
            },String::class.java)

            conn.on("GetChatUsers",{users : Any? ->
                (users as MutableList<String>).forEach {
                    chatUsers.value?.add(it)
                    usersCount.postValue(usersCount.value?.plus(1))
                }
                Log.i("mhy","get chat users")
            }, mutableListOf<String>()::class.java)


        }

    }

    fun SetOnline(type :String){
        connection.value?.send("SetOnline",type)
        Log.i("mhy","setOnline")
    }

    fun StopConnenction(){
        chatUsers.value?.clear()
        broadcastUsers.value?.clear()
        chatRoomUsers.value?.clear()
        usersCount.value = 0
        connection.value?.stop()
        //todo
    }

    fun SendMessage(msg: Message){
        if (msg.contentType != "file"){
            chatRecords.value?.add(msg)
        }
        connection.value?.send("SendMessage",msg)
        Log.i("mhy","send: "+ msg.toString())
    }

    fun AddUser(item : String){
        chatUsers.value!!.add(item)
        broadcastUsers.value!!.add(item)
        chatRoomUsers.value!!.add(item)
        usersCount.value = usersCount.value?.plus(1)
    }



}