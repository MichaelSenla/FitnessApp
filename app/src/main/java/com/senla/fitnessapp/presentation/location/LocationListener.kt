package com.senla.fitnessapp.presentation.location

import android.location.LocationListener

class LocationListener(val location: Location): LocationListener{

    override fun onLocationChanged(location: android.location.Location) {
        //this.location.onLocationChanged()
    }
}