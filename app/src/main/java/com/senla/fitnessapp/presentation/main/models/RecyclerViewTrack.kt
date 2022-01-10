package com.senla.fitnessapp.presentation.main.models

data class RecyclerViewTrack(
    val startTime: String,
    val distance: String,
    val joggingTime: String,
    val startLongitude: Double = 0.0,
    val startLatitude: Double = 0.0,
    val finishLongitude: Double = 0.0,
    val finishLatitude: Double = 0.0
)
