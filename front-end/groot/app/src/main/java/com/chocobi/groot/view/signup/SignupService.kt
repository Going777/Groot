package com.chocobi.groot.view.signup

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SignupService {

    //    @FormUrlEncoded
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    @POST("/api/users")
    fun requestSignup(

//        raw data json으로 보낼 때
        @Body params: SignupRequest,

//        form data로 보낼 때
//    @Field("userId") textId:String,
//    @Field("nickName") textName:String,
//    @Field("password") textPw:String,
//    @Field("profile") textProfile:String,
    ): Call<Signup>
}

class SignupRequest internal constructor(
    val userId: String,
    val nickName: String,
    val password: String
)