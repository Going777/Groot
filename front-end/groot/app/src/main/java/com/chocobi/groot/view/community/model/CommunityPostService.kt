package com.chocobi.groot.view.community

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CommunityPostService {

    @FormUrlEncoded
    @POST("/articles/") // 요청 url
    fun requestLogin(
//        input 정의
        @Field("title") titleInput:String,
        @Field("content") contentInput:String,
    ) : Call<CommunityPostResponse> // output 정의
}