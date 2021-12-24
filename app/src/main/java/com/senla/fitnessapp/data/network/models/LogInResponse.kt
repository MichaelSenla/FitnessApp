package com.senla.fitnessapp.data.network.models

data class LogInResponse(
    val status: String, val token: String, val firstName: String, val lastName: String)
