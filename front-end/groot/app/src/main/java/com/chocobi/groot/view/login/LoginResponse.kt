package com.chocobi.groot.view.login

// output을 만든다 : response

data class LoginResponse(
    var accessToken : String,
    var refreshToken : String,
    var result : String,
    var msg : String,
)

data class SubscribeResponse(
    val receiver: Int,
    val content: String,
    val page: String,
    val contentId: Int,
    val read: Boolean,
    val notificationId: Int,
)

