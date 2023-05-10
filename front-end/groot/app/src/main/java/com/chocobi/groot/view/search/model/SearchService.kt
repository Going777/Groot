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
    fun requestSearchPlants(
        @Query("name") name: String? = null,
        @Query("difficulty") difficulty1: String? = null,
        @Query("difficulty") difficulty2: String? = null,
        @Query("difficulty") difficulty3: String? = null,
        @Query("lux") lux1: String? = null,
        @Query("lux") lux2: String? = null,
        @Query("lux") lux3: String? = null,
        @Query("growth") growth1: String? = null,
        @Query("growth") growth2: String? = null,
        @Query("growth") growth3: String? = null,
        @Query("growth") growth4: String? = null,
        @Query("growth") growth5: String? = null,
        @Query("growth") growth6: String? = null,
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
    @POST("/api/plants/identify")
    fun identifyPlant(
        @Part filePart: MultipartBody.Part
    ): Call<PlantIdentifyResponse>

    @GET("/api/plants/{plantId}/introductions")
    fun getRecomm(
        @Path("plantId") plantId: Int
    ): Call<PlantIdentifyResponse>
}