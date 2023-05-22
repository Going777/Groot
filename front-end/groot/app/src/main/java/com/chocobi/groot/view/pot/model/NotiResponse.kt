package com.chocobi.groot.view.pot.model

data class NotiResponse(
    val result: String,
    val msg: String,
    val notification: Notification
)

data class Notification(
    val total: Int,
    val content: List<NotiMessage>
)

data class NotiMessage(
    val receiver: Int,
    val content: String,
    val page: String,
    val contentId: Int?,
    val chattingRoomId: Int?,
    val isRead: Boolean,
    val id: Int,
    val createDate: DateTime
)
