package com.chocobi.groot.view.login

import com.chocobi.groot.view.signup.Signup
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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
    ): Call<Login>
}

class LoginRequest internal constructor(
    val userId: String,
    val password: String
)