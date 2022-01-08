package com.senla.fitnessapp.data.network

import com.senla.fitnessapp.data.network.models.*
import com.senla.fitnessapp.data.network.models.getAllTracks.GetAllTracksRequest
import com.senla.fitnessapp.data.network.models.getAllTracks.getAllTracksResponse.GetAllTracksResponse
import com.senla.fitnessapp.data.network.models.saveTrack.saveTrackRequest.SaveTrackRequest
import com.senla.fitnessapp.data.network.models.saveTrack.SaveTrackResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitService {

    @POST("lesson-26.php")
    fun userLogIn(
        @Query("method") query: String,
        @Body request: LogInRequest
    ): Single<LogInResponse>

    @POST("lesson-26.php")
    fun registerUser(
        @Query("method") query: String,
        @Body request: RegisterRequest
    ): Single<RegisterResponse>

    @POST("lesson-26.php")
    fun saveTrack(
        @Query("method") query: String,
        @Body request: SaveTrackRequest
    ): Single<SaveTrackResponse>

    @POST("lesson-26.php")
    fun getAllTracks(
        @Query("method") query: String,
        @Body request: GetAllTracksRequest
    ): Single<GetAllTracksResponse>
}