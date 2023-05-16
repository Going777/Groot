package com.chocobi.groot.view.chat.model

import retrofit2.Call
import retrofit2.http.GET

data class ChatUser(
    var uId: String,
    var nickname: String,
    var profile: String,
    var userPK: Int
) {
    constructor(): this("", "", "", 0)
}

interface ChatUserListService {
    @GET("/api/chattings/list")
    fun requestChatUserList(

    ): Call<ChatUserListResponse>
}