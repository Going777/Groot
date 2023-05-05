package com.chocobi.groot.view.addpot.model

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PotService {

    @Multipart
    @POST("api/pots")
    fun addPot(
        @Part("pot") metadata: PotRequest,
        @Part filePart: MultipartBody.Part?
    ): Call<PotResponse>
}



class PotRequest internal constructor(
    val plantId: Int,
    val potName: String,
    val temperature: Number?,
    val illuminance: Number?,
    val humidity: Number?,
)