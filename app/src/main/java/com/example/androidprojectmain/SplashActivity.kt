package com.example.androidprojectmain


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private val splashDuration = 3000L // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        videoView = findViewById(R.id.videoView)
        progressBar = findViewById(R.id.loadingBar)


        val videoPath = "android.resource://$packageName/${R.raw.splash}"
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)


        videoView.setOnPreparedListener { mp ->
            mp.isLooping = false
            videoView.start()
        }

         val handler = Handler(Looper.getMainLooper())
        var progress = 0
        val runnable = object : Runnable {
            override fun run() {
                if (progress < 3) {
                    progress++
                    progressBar.progress = progress
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(runnable)

         Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, splashDuration)
    }
}
