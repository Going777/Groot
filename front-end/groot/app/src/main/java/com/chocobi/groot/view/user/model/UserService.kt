package com.chocobi.groot.view.user.model

import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import com.chocobi.groot.view.login.LoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


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

    @GET("/api/articles/category/{category}") // 요청 url
    fun requestUserArticleList(
//        input 정의
        @Path("category") categoryInput:String,
        @Query("page") pageInput:Int,
        @Query("size") sizeInput:Int,

        ) : Call<CommunityArticleListResponse> // output 정의

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