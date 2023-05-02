package com.chocobi.groot.view.user.model

import com.chocobi.groot.view.login.LoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface UserService {

    @GET("/api/users/logout")
    fun logout(
    ): Call<LogoutResponse>

    @Headers("content-type: application/json")
    @DELETE("/api/users")
    fun deleteUser(
        @Header("Authorization") accessToken: String
    ): Call<LogoutResponse>



    @GET("/api/users")
    fun getUser(
    ): Call<GetUserResponse>

    @Headers("content-type: application/json")
    @POST("/api/users/refresh")
    fun refresh(
        @Body params: RefreshRequest
    ): Call<RefreshResponse>
}

class RefreshRequest internal constructor(
    val accessToken: String,
    val refreshToken: String
)