package com.example.newsapptask

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.example.newsapptask.common.Constants
import com.example.newsapptask.presentation.first_time_page.ui.OnBoardActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar!!.hide()
        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME , Context.MODE_PRIVATE)
        val firstTimeOpen = sharedPreferences.getBoolean(Constants.FIRST_TIME_OPEN , true)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Handler().postDelayed({
            if (firstTimeOpen){
                startActivity(Intent(this, OnBoardActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }, 2000)
    }
}