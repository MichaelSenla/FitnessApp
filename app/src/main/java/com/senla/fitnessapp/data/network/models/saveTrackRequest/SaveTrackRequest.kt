package com.senla.fitnessapp.data.network.models.saveTrackRequest

import kotlin.random.Random

data class SaveTrackRequest(
    val token: String, val id: Int = generateId(), val beginsAt: Long, val time: Int,
    val distance: Int, val points: List<Point>
) {

    companion object {
        private const val ID_BOUND = 100_000

        fun generateId(): Int {
            return Random.nextInt(ID_BOUND)
        }
    }
}
