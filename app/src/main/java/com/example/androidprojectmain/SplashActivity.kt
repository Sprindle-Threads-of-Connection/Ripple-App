package com.example.androidprojectmain

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var splashImage: ImageView
    private lateinit var progressBar: ProgressBar
    private val splashDuration = 2000L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        splashImage = findViewById(R.id.appLogo)
        progressBar = findViewById(R.id.loadingBar)

        // Animate progress bar
        val handler = Handler(Looper.getMainLooper())
        var progress = 0
        val runnable = object : Runnable {
            override fun run() {
                if (progress < 100) {
                    progress += 33
                    progressBar.progress = progress
                    handler.postDelayed(this, 667) // ~3 steps to complete
                }
            }
        }
        handler.post(runnable)

        // Navigate to AuthActivity after splash duration
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }, splashDuration)
    }
}
