package com.chocobi.groot.view.user.model

import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query


interface UserService {

    @GET("/api/users/logout")
    fun logout(
    ): Call<BasicResponse>

    @Headers("content-type: application/json")
    @DELETE("/api/users")
    fun deleteUser(
        @Header("Authorization") accessToken: String
    ): Call<BasicResponse>

    @GET("/api/users")
    fun getUser(
    ): Call<GetUserResponse>

    @GET("/api/users/mypage/article")
    fun requestUserArticleList(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<CommunityArticleListResponse>


    @GET("/api/users/mypage/bookmark")
    fun requestUserBookmarkList(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<CommunityArticleListResponse>

    @PUT("api/users/password")
    fun changePassword(
        @Body params: PasswordRequest
    ): Call<BasicResponse>

    @Multipart
    @PUT("api/users")
    fun changeProfile(
        @Part("userProfileDTO") metadata: ProfileRequest,
        @Part filePart: MultipartBody.Part?
    ): Call<BasicResponse>


    @Headers("content-type: application/json")
    @POST("/api/users/refresh")
    fun refresh(
        @Body params: RefreshRequest
    ): Call<RefreshResponse>
}

class RefreshRequest internal constructor(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String


)

class PasswordRequest internal constructor(
    val userPK: Int,
    val password: String,
    val newPassword: String
)

class ProfileRequest internal constructor(
    val userPK: Int,
    val nickName: String,
    val profile: String?
)

