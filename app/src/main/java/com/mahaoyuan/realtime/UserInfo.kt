package com.mahaoyuan.realtime

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.Single
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
    val chatRecords = MutableLiveData<MutableList<String>>()
    val broadcastRecords = MutableLiveData<MutableList<String>>()
    val chatRoomRecords = MutableLiveData<MutableList<String>>()

    init {
        mode.value = ""
        connection.value = null
        userEmail.value = ""
        token.value = ""
        chatTo.value = ""
        chatUsers.value = mutableListOf("mhy", "ly", "zhq", "lqf")
        broadcastUsers.value = mutableListOf()
        chatRoomUsers.value = mutableListOf()
        usersCount.value = 0
        chatRecords.value = mutableListOf()
        broadcastRecords.value = mutableListOf()
        chatRoomRecords.value = mutableListOf()
        Log.i("mhy", "init UserInfo")

    }

    fun BuildConnection(){
        connection.value = HubConnectionBuilder.create("http://10.0.2.2:12165/Hubs/MessageHub")
            .withAccessTokenProvider(Single.defer({ Single.just(token.value) })).build()
    }

    fun StartConnection(){
        connection.value?.start()
    }


}