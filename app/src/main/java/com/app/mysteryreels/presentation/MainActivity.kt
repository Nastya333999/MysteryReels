package com.app.mysteryreels.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.app.mysteryreels.R
import com.app.mysteryreels.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Thread.sleep(2000)
        installSplashScreen()

        setContentView(R.layout.activity_main)
    }





}