package com.senla.fitnessapp.data.network

import com.senla.fitnessapp.data.network.models.LogInResponse
import com.senla.fitnessapp.data.network.models.RegisterResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitService {

    @FormUrlEncoded
    @POST("lesson-26.php?method=login")
    fun userLogIn(
        @Field("email") email: String,
        @Field("password") password: String): Observable<LogInResponse>

    @FormUrlEncoded
    @POST("lesson-26.php?method=register")
    fun registerUser(
        @Field("email") email: String,
        @Field("firstName") firstName: String,
        @Field("lastName") lastName: String,
        @Field("password") password: String): Observable<RegisterResponse>
}