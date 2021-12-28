package com.senla.fitnessapp.common.models

import java.util.*

data class Notification(
    var id: Int = generateId(),
    var title: String = "",
    var time: String = ""
) {

    companion object {
        private const val ID_BOUND = 10000

        private fun generateId(): Int {
            return Random().nextInt(ID_BOUND)
        }
    }
}