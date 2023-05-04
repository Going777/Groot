package com.chocobi.groot.view.search.model

import android.util.Log
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchService {

    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    @GET("/api/plants")
    fun searchPlants(
        @Query("name") name: String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("lux") lux: String? = null,
        @Query("growth") growth: String? = null,
        @Query("page") page: Int? = null,
    ): Call<PlantSearchResponse>

    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    @GET("/api/plants/{plantId}")
    fun getPlantDetail(
        @Path("plantId") plantId: Int
    ): Call<PlantDetailResponse>

    @Multipart
    @POST("api/plants/identify")
    fun identifyPlant(
        @Part filePart: MultipartBody.Part
    ): Call<PlantIdentifyResponse>


}