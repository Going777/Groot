package com.chocobi.groot.view.signup

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignupService {

    @FormUrlEncoded
    @POST("/users/")
    fun requestSignup(
        @Field("user_id") textId:String,
        @Field("nickname") textName:String,
        @Field("password") textPw:String,
        @Field("profile") textProfile:String,
    ) : Call<Signup>
}