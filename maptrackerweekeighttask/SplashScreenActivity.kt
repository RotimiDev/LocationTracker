package com.olamachia.maptrackerweekeighttask

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)

        supportActionBar?.hide()


        //delay movement to the next activity by 2 seconds
        Handler().postDelayed({
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)


    }
}