package com.senla.fitnessapp.data.network

import com.senla.fitnessapp.data.network.models.LogInResponse
import com.senla.fitnessapp.data.network.models.RegisterRequest
import com.senla.fitnessapp.data.network.models.RegisterResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface RetrofitService {

    @POST("lesson-26.php")
    fun userLogIn(
        @Query("method") query: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Single<LogInResponse>

    @POST("lesson-26.php")
    fun registerUser(
        @Query("method") query: String,
        @Body request: HashMap<String, String>): Single<RegisterResponse>
}