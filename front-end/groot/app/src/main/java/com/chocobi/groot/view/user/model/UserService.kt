package com.chocobi.groot.view.user.model

import com.chocobi.groot.view.signup.DupIdResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface UserService {
    @Headers("content-type: application/json")
    @GET("/api/users")
    fun getUser(
        @Header("Authorization") accessToken: String
    ): Call<GetUserResponse>
}