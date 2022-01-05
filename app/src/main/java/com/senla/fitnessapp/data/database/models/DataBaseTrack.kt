package com.senla.fitnessapp.data.database.models

import java.util.*

data class DataBaseTrack(val id: Int = generateId(), var distance: String = "") {
    companion object {
        private const val ID_BOUND = 100000

        private fun generateId(): Int {
            return Random().nextInt(ID_BOUND)
        }
    }
}
