package com.senla.fitnessapp.data.network

import com.senla.fitnessapp.data.network.models.LogInRequest
import com.senla.fitnessapp.data.network.models.LogInResponse
import com.senla.fitnessapp.data.network.models.RegisterRequest
import com.senla.fitnessapp.data.network.models.RegisterResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitService {

    @POST("lesson-26.php")
    fun userLogIn(
        @Query("method") query: String,
        @Body request: LogInRequest): Single<LogInResponse>

    @POST("lesson-26.php")
    fun registerUser(
        @Query("method") query: String,
        @Body request: RegisterRequest): Single<RegisterResponse>
}