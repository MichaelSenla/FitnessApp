package com.senla.fitnessapp.presentation.jogging.location

import android.location.Location
import android.location.LocationListener

class LocationListener: LocationListener {
    var gpsLocation: GpsLocation? = null

    override fun onLocationChanged(location: Location) {
        gpsLocation?.onLocationChanged(location)
    }
}