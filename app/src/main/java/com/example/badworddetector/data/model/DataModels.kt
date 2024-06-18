package com.example.badworddetector.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserResponse(
    val payload: UserPayload,
    val message: String
)

data class UserPayload(
    val status_code: Int,
    val data: UserData?
)

data class UserData(
    val uid: String?,
    val name: String?,
    val email: String?,
    val password: String?,
    val createdAt: String?
)

data class LoginResponse(
    val payload: LoginPayload,
    val message: String
)

data class LoginPayload(
    val status_code: Int,
    val data: LoginData?
)

data class LoginData(
    val userId: String,
    val name: String,
    val token: String
)

data class PredictRequest(
    val text: String
)

data class PredictResponse(
    val status: Boolean,
    val result: String,
    val error: String? = null
)