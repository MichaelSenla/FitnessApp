package com.senla.fitnessapp.data.database.models

import java.util.*

data class DataBaseTrack(
    val id: Int = generateId(),
    var startTime: Long = 0,
    var distance: String = "",
    var joggingTime: Long = 0,
    var startLongitude: Double = 37.6377,
    var startLatitude: Double = 55.7305,
    var finishLongitude: Double = 37.6377,
    var finishLatitude: Double = 55.7305
) {
    companion object {
        private const val ID_BOUND = 100000

        private fun generateId(): Int {
            return Random().nextInt(ID_BOUND)
        }
    }
}
