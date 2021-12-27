package com.senla.fitnessapp.data.network.models

data class RegisterRequest(
    val email: String, val firstName: String, val lastName: String,
    val password: String
)