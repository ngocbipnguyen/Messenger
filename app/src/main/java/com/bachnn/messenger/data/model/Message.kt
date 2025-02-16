package com.bachnn.messenger.data.model

data class Message(
    val idFrom: String,
    val idTo: String,
    val timestamp: String,
    val content: String,
    val type: String,
    var emoticonType: String
)