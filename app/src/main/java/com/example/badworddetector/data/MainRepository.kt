package com.example.badworddetector.data

import android.util.Log
import androidx.room.Room
import com.example.badworddetector.data.api.ApiConfig.mlApiService
import com.example.badworddetector.data.api.ApiService
import com.example.badworddetector.data.api.MlApiService
import com.example.badworddetector.data.model.LoginRequest
import com.example.badworddetector.data.model.LoginResponse
import com.example.badworddetector.data.model.PredictRequest
import com.example.badworddetector.data.model.PredictResponse
import com.example.badworddetector.data.model.RegisterRequest
import com.example.badworddetector.data.model.UserResponse
import com.example.badworddetector.database.AppDatabase
import com.example.badworddetector.database.UserInput
import com.example.badworddetector.database.UserInputDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository(
    private val apiService: ApiService,
    private val mlApiService: MlApiService,
    private val userInputDao: UserInputDao
) {

    fun registerUser(name: String, email: String, password: String, callback: (Result<UserResponse>) -> Unit) {
        val request = RegisterRequest(name, email, password)
        val call = apiService.register(request)

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    }
                } else {
                    callback(Result.failure(Exception("Failed to register user: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun loginUser(email: String, password: String, callback: (Result<LoginResponse>) -> Unit) {
        val request = LoginRequest(email, password)
        val call = apiService.login(request)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    }
                } else {
                    callback(Result.failure(Exception("Failed to login user: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun getUserData(uid: String, token: String, callback: (Result<UserResponse>) -> Unit) {
        val call = apiService.getUser(uid, "Bearer $token")
        Log.d("MainRepository", "Fetching user data for UID: $uid")

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("MainRepository", "User data fetched successfully: ${it.payload.data}")
                        callback(Result.success(it))
                    }
                } else {
                    val errorMessage = "Failed to fetch user data: ${response.message()}"
                    Log.e("MainRepository", errorMessage)
                    callback(Result.failure(Exception(errorMessage)))
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                val errorMessage = "Error fetching user data: ${t.message}"
                Log.e("MainRepository", errorMessage, t)
                callback(Result.failure(Exception(errorMessage, t)))
            }
        })
    }

    fun predictText(text: String, email: String, callback: (Result<PredictResponse>) -> Unit) {
        val request = PredictRequest(text)
        val call = mlApiService.predict(request)

        call.enqueue(object : Callback<PredictResponse> {
            override fun onResponse(call: Call<PredictResponse>, response: Response<PredictResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        saveUserInput(UserInput(text = text, result = it.result, email = email))
                        callback(Result.success(it))
                    }
                } else {
                    callback(Result.failure(Exception("Failed to predict text: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    private fun saveUserInput(userInput: UserInput) {
        CoroutineScope(Dispatchers.IO).launch {
            userInputDao.insert(userInput)
        }
    }

    suspend fun getUserInputs(): List<UserInput> {
        return userInputDao.getAll()
    }

    suspend fun getUserInputsByEmail(email: String): List<UserInput> {
        return userInputDao.getByEmail(email)
    }
}
