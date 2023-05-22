package com.chocobi.groot.view.chat.model

data class ChatUserListResponse(
    val result: String,
    val msg: String,
    val chatting: List<Chat>
)

data class Chat(
    val userPK: Int,
    val nickName: String,
    val profile: String,
    val roomId: String
)

data class ChatInfoResponse(
    val result: String,
    val msg: String,
    val chatting: ChatRoom
)

data class ChatRoom(
    val userPK: Int,
    val nickName: String,
    val profile: String,
    val receive: Boolean
)
