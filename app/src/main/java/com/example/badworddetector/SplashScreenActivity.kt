package com.example.badworddetector

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.badworddetector.data.preference.UserPreference
import com.example.badworddetector.ui.login.LoginActivity


class SplashScreenActivity : AppCompatActivity() {

    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        userPreference = UserPreference(this)
        enableEdgeToEdge()

        Handler().postDelayed({
            if (userPreference.getUserToken() != null) {
                val intent = Intent(this@SplashScreenActivity, BottomNavigationActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, SPLASH_SCREEN_TIMEOUT.toLong())
    }

    companion object {
        private const val SPLASH_SCREEN_TIMEOUT = 2000 // 2 seconds
    }
}