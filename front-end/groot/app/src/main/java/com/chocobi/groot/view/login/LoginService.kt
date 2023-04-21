package com.chocobi.groot.view.login

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginService {

    @FormUrlEncoded
    @POST("/users/login/") // 요청 url
    fun requestLogin(
//        input 정의
        @Field("id") textId:String,
        @Field("password") textPw:String,
    ) : Call<Login> // output 정의
}