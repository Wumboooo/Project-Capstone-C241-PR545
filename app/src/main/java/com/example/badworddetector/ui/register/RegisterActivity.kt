package com.example.badworddetector.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import com.example.badworddetector.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var userPreference: UserPreference
    private lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        enableEdgeToEdge()

        userPreference = UserPreference(this)
        mainRepository = ApiConfig.provideMainRepository(this)

        val nameEditText = findViewById<EditText>(R.id.name)
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val registerButton = findViewById<Button>(R.id.btn_register)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            mainRepository.registerUser(name, email, password) { result ->
                result.onSuccess { response ->
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }.onFailure { exception ->
                    Toast.makeText(this, "Registration failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val loginButton = findViewById<Button>(R.id.btn_have_account)

        loginButton.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, BottomNavigationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}