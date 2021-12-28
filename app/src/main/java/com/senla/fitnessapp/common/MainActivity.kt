package com.senla.fitnessapp.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.senla.fitnessapp.R
import com.senla.fitnessapp.presentation.main.MainFragment
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
            .replace(R.id.fragmentContainer, MainFragment()).commit()
    }

    private fun setSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}