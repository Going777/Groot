package com.chocobi.groot.view.login

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST



interface LoginService {

    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    @POST("/api/users/login")
    fun requestLogin(
        @Body params: LoginRequest,
    ): Call<LoginResponse>

    @GET("/api/notifications/subscribe")
    fun requestSubscribe():Call<SubscribeResponse>

    @POST("/api/users/oauth")
    fun requestSocialLogin(
        @Body params: SocialLoginRequest
    ): Call<LoginResponse>

}

class LoginRequest internal constructor(
    val userId: String,
    val password: String,
    val firebaseToken: String
)

class SocialLoginRequest internal constructor(
    val oauthProvider: String,
    val accessToken: String,
    val nickName: String? = null,
    val firebaseToken: String
)
