package com.chocobi.groot

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginService {

    @FormUrlEncoded
    @POST("/accounts/login/") // 요청 url
    fun requestLogin(
//        input 정의
        @Field("username") userId:String,
        @Field("password") userPw:String,
    ) : Call<Login> // output 정의
}