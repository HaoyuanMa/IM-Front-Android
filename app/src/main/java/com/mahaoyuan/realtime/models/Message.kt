package com.mahaoyuan.realtime.models

data class Message(
        var type :String = "",
        var from :String = "",
        var to :MutableList<String> = mutableListOf(),
        var contentType :String = "",
        var content :String = "",
        var fileSize :Long = 0
)
