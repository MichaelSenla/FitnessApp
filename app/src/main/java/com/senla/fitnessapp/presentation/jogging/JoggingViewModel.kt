package com.senla.fitnessapp.presentation.jogging

import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt

class JoggingViewModel : ViewModel() {

    fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()

        val minutes = resultInt % 86400 % 3600 / 60 / 10
        val seconds = resultInt % 86400 % 3600 % 600 / 10
        val milliseconds = resultInt % 86400 % 3600 % 60 % 10

        return makeTimeString(minutes, seconds, milliseconds)
    }

    private fun makeTimeString(minutes: Int, seconds: Int, milliseconds: Int): String =
        String.format("%02d:%02d:%02d", minutes, seconds, milliseconds)
}