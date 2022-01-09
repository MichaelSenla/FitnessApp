package com.senla.fitnessapp.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.senla.fitnessapp.R
import com.senla.fitnessapp.presentation.jogging.JoggingFragment.Companion.lastLocation
import com.senla.fitnessapp.presentation.main.MainFragment
import com.senla.fitnessapp.presentation.splash.SplashFragment
import com.senla.fitnessapp.presentation.track.TrackFragment.Companion.map
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
            .replace(R.id.fragmentContainer, SplashFragment()).commit()
    }

    private fun setSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}