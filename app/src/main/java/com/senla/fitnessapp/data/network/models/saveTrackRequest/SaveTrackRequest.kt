package com.senla.fitnessapp.data.network.models.saveTrackRequest

data class SaveTrackRequest(
    val token: String, val id: Int? = null, val beginsAt: Long, val time: Int,
    val distance: Int, val points: List<Point>
)
