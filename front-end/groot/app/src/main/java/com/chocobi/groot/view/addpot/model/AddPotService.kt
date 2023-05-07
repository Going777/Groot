package com.chocobi.groot.view.addpot.model

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AddPotService {

    @Multipart
    @POST("api/pots")
    fun addPot(
        @Part("pot") metadata: AddPotRequest,
        @Part filePart: MultipartBody.Part?
    ): Call<AddPotResponse>
}



class AddPotRequest internal constructor(
    val plantId: Int,
    val potName: String,
    val temperature: Number?,
    val illuminance: Number?,
    val humidity: Number?,
)