package com.example.talktango2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.logging.Handler

class SplashScreen : AppCompatActivity() {
    private val SPLASH_DURATION: Long = 2000 // 2 seconds


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        // Delay for the splash screen and then navigate to the login activity
        android.os.Handler().postDelayed({
            val intent = Intent(this@SplashScreen, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finish the splash screen activity to prevent going back to it
        }, SPLASH_DURATION)
    }
}