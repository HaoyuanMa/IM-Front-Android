package com.mahaoyuan.realtime.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel(){
    val chatUsers = MutableLiveData<MutableSet<String>>()

    init {
        chatUsers.value = mutableSetOf()

    }


}