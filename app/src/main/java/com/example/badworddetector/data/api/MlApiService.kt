package com.example.badworddetector.data.api

import com.example.badworddetector.data.model.PredictRequest
import com.example.badworddetector.data.model.PredictResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MlApiService {
    @POST("/predict")
    fun predict(@Body request: PredictRequest): Call<PredictResponse>
}