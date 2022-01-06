package com.senla.fitnessapp.presentation.jogging.location

import android.location.Location

interface GpsLocation {
    fun onLocationChanged(location: Location)
}