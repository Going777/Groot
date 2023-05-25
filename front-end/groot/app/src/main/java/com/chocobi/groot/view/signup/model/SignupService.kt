package com.chocobi.groot.view.signup

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SignupService {

    @POST("/api/users")
    fun requestSignup(
        @Body params: SignupRequest,
    ): Call<SignupResponse>
}

class SignupRequest internal constructor(
    val userId: String,
    val nickName: String,
    val password: String,
    val firebaseToken: String?,
)


interface DupIdService {
    @GET("/api/users/userId/{userId}")
    fun requestDupId(
        @Path("userId") textId: String
    ): Call<DupIdResponse>
}

interface DupNameService {
    @GET("/api/users/nickname/{nickname}")
    fun requestDupName(
        @Path("nickname") textName: String
    ): Call<DupNameResponse>
}
