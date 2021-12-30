package com.senla.fitnessapp.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.senla.fitnessapp.R
import com.senla.fitnessapp.presentation.jogging.JoggingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar()
        navigateToSplashFragment()
    }

    private fun navigateToSplashFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, JoggingFragment()).commit()
    }

    private fun setSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}