package com.senla.fitnessapp.presentation.location

import android.location.Location
import android.location.LocationListener

class LocationListener: LocationListener {
    lateinit var gpsLocation: GpsLocation

    override fun onLocationChanged(location: Location) {
        gpsLocation.onLocationChanged(location)
    }
}