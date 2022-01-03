package com.senla.fitnessapp.presentation.location

import android.location.Location

interface GpsLocation {
    fun onLocationChanged(location: Location)
}