package com.Shoshin.app.data.models

data class FriendRequest(
    val requestId: String,
    val fromUserId: String,
    val fromUserName: String,
    val toUserId: String,
    val sentDate: Long,
    val status: String // pending, accepted, declined
)
