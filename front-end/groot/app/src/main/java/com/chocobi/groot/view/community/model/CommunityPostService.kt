package com.chocobi.groot.view.community

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CommunityPostService {

    @Multipart
    @POST("/api/articles/") // 요청 url
    fun requestCommunityPost(
//        input 정의
        @Part("articleDTO") metadata: ArticlePostRequest,
        @Part images1: MultipartBody.Part?,
        @Part images2: MultipartBody.Part?,
        @Part images3: MultipartBody.Part?,
    ) : Call<CommunityPostResponse> // output 정의
}

class ArticlePostRequest internal constructor(
    val userPK: Int,
    val category: String,
    val title: String,
    val content: String,
    val tags: MutableList<String>,
    val shareRegion: String?,
    val shareStatus: Boolean,
//    val shareStatus: Boolean?,
//    val shareRegion: String?,
//    val tags: Array<String>?
)