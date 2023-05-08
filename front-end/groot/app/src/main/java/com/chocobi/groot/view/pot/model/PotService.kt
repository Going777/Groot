package com.chocobi.groot.view.pot.model

import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.MsgResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface PotService {
    @GET("/api/pots/{potId}")
    fun getPotDetail(
        @Path("potId") potId:Int
    ): Call<PotResponse>

    @GET("/api/pots")
    fun getPotList(
    ): Call<PotListResponse>

    @DELETE("/api/pots/{potId}")
    fun deletePot(
        @Path("potId") potId:Int
    ): Call<MsgResponse>

    @Multipart
    @POST("/api/diaries")
    fun requestPostDiary(
        @Part("postData") metaData: DiaryRequest,
        @Part filePart: MultipartBody.Part?
    ): Call<BasicResponse>
}