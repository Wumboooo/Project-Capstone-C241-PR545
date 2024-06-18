package com.example.badworddetector.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.badworddetector.BottomNavigationActivity
import com.example.badworddetector.R
import com.example.badworddetector.data.MainRepository
import com.example.badworddetector.data.api.ApiConfig
import com.example.badworddetector.data.preference.UserPreference
import com.example.badworddetector.ui.register.RegisterActivity
import com.google.android.material.progressindicator.LinearProgressIndicator

class LoginActivity : AppCompatActivity() {

    private lateinit var userPreference: UserPreference
    private lateinit var mainRepository: MainRepository
    private lateinit var progressIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableEdgeToEdge()

        userPreference = UserPreference(this)
        mainRepository = ApiConfig.provideMainRepository(this)

        progressIndicator = findViewById(R.id.progressIndicator)

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.btn_login)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            showLoading(true)
            mainRepository.loginUser(email, password) { result ->
                showLoading(false)
                result.onSuccess { response ->
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val token = response.payload.data?.token
                    val userId = response.payload.data?.userId
                    if (token != null && userId != null) {
                        userPreference.saveUserToken(token)
                        userPreference.saveUserId(userId)
                        navigateToMainScreen()
                    }
                }.onFailure { exception ->
                    Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val registerButton = findViewById<Button>(R.id.btn_havent_account)
        registerButton.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        window.setFlags(
            if (isLoading) WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE else 0,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun navigateToMainScreen() {
        val intent = Intent(this, BottomNavigationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}