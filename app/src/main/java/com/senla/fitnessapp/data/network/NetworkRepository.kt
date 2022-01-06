package com.senla.fitnessapp.data.network

import com.senla.fitnessapp.data.network.models.*
import com.senla.fitnessapp.data.network.models.getAllTracks.GetAllTracksRequest
import com.senla.fitnessapp.data.network.models.getAllTracks.getAllTracksResponse.GetAllTracksResponse
import com.senla.fitnessapp.data.network.models.saveTrackRequest.SaveTrackRequest
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val retrofitService: RetrofitService
) {
    fun userLogIn(query: String, logInRequest: LogInRequest): Single<LogInResponse> =
        retrofitService.userLogIn(query, logInRequest)

    fun registerUser(query: String, registerRequest: RegisterRequest): Single<RegisterResponse> =
        retrofitService.registerUser(query, registerRequest)

    fun saveTrack(query: String, saveTrackRequest: SaveTrackRequest): Single<SaveTrackResponse> =
        retrofitService.saveTrack(query, saveTrackRequest)

    fun getAllTracks(query: String, request: GetAllTracksRequest): Single<GetAllTracksResponse> =
        retrofitService.getAllTracks(query, request)
}