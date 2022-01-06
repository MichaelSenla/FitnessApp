package com.senla.fitnessapp.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.senla.fitnessapp.R
import com.senla.fitnessapp.presentation.jogging.JoggingFragment
import com.senla.fitnessapp.presentation.jogging.JoggingFragment.Companion.lastLocation
import com.senla.fitnessapp.presentation.notification.NotificationFragment
import com.senla.fitnessapp.presentation.splash.SplashFragment
import com.senla.fitnessapp.presentation.track.AddMapsMarkers
import com.senla.fitnessapp.presentation.track.TrackFragment
import com.senla.fitnessapp.presentation.track.TrackFragment.Companion.googleMap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity(), AddMapsMarkers {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar()
        navigateToSplashFragment()
    }

    private fun navigateToSplashFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, TrackFragment()).commit()
    }

    private fun setSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun addStartMarker(latLng: LatLng) {
        googleMap?.addMarker(MarkerOptions().position(
            LatLng(lastLocation!!.latitude, lastLocation!!.longitude)))
    }

    override fun addFinishMarker(latLng: LatLng) {

    }
}