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

    @POST("/api/users/kakao")
    fun requestKakaoLogin(
        @Body accessToken: String,
        @Body nickName: String? = null,
        @Body firebaseToken: String
    ): Call<LoginResponse>

}

class LoginRequest internal constructor(
    val userId: String,
    val password: String
)