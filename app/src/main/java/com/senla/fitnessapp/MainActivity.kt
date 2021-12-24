package com.senla.fitnessapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.senla.fitnessapp.ui.splash.SplashFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SplashFragment()).commit()
    }
}