package com.example.badworddetector.data.api

import com.example.badworddetector.data.model.LoginRequest
import com.example.badworddetector.data.model.LoginResponse
import com.example.badworddetector.data.model.PredictRequest
import com.example.badworddetector.data.model.PredictResponse
import com.example.badworddetector.data.model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("/v1/auth/register")
    fun register(
        @Body registerRequest: com.example.badworddetector.data.model.RegisterRequest
    ): Call<UserResponse>

    @POST("/v1/auth/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @GET("/v1/auth/user/{uid}")
    fun getUser(
        @Path("uid") uid: String,
        @Header("Authorization") token: String
    ): Call<UserResponse>
}