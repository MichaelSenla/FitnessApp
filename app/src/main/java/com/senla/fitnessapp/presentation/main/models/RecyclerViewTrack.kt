package com.senla.fitnessapp.presentation.main.models

data class RecyclerViewTrack(
    val startTime: String,
    val distance: String,
    val joggingTime: String,
    val startLongitude: Double = 37.6377,
    val startLatitude: Double = 55.7305,
    val finishLongitude: Double = 55.7305,
    val finishLatitude: Double = 37.6377
)
