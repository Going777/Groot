package com.chocobi.groot.view.user.model

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface UserService {
    @Headers("content-type: application/json")
    @GET("/api/users/logout")
    fun logout(
        @Header("Authorization") accessToken: String
    ): Call<LogoutResponse>

    @Headers("content-type: application/json")
    @DELETE("/api/users")
    fun deleteUser(
        @Header("Authorization") accessToken: String
    ): Call<LogoutResponse>

    @Headers("content-type: application/json")
    @GET("/api/users")
    fun getUser(
        @Header("Authorization") accessToken: String
    ): Call<GetUserResponse>
}
