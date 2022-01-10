package com.senla.fitnessapp.presentation.main

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.senla.fitnessapp.R
import com.senla.fitnessapp.common.Constants.SHARED_PREFERENCES_TOKEN_KEY
import com.senla.fitnessapp.data.database.SQLiteRepository
import com.senla.fitnessapp.data.database.models.DataBaseSavedTrack
import com.senla.fitnessapp.data.database.models.DataBaseTrack
import com.senla.fitnessapp.data.network.NetworkRepository
import com.senla.fitnessapp.data.network.models.getAllTracks.GetAllTracksRequest
import com.senla.fitnessapp.data.network.models.getAllTracks.getAllTracksResponse.GetAllTracksResponse
import com.senla.fitnessapp.data.network.models.getAllTracks.getAllTracksResponse.NetworkTrack
import com.senla.fitnessapp.data.network.models.saveTrack.saveTrackRequest.Point
import com.senla.fitnessapp.data.network.models.saveTrack.saveTrackRequest.SaveTrackRequest
import com.senla.fitnessapp.presentation.main.MainFragment.Companion.GET_ALL_TRACKS_FROM_SERVER_QUERY
import com.senla.fitnessapp.presentation.main.models.RecyclerViewTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val applicaiton: Application,
    val networkRepository: NetworkRepository,
    val sqLiteRepository: SQLiteRepository,
    val sharedPreferences: SharedPreferences
) : AndroidViewModel(applicaiton) {

    companion object {
        private const val TIME_FORMAT = "dd.MM.yyyy HH:mm:ss"
        private const val CONVERT_TO_HUNDRED_OF_MILLISECONDS_NUMBER = 100
        private const val CONVERT_TO_SECONDS_NUMBER = 1000
        private const val CONVERT_TO_MINUTES_NUMBER = 60_000
        private const val CONVERT_TO_HUNDRED_OF_MILLISECONDS_NUMBER_SCALE = 10
        private const val CONVERT_TO_SECONDS_NUMBER_SCALE = 60
        private const val CONVERT_TO_MINUTES_NUMBER_SCALE = 60
        private const val SAVE_TRACK_ON_SERVER_QUERY = "save"
    }

    private var compositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var networkTracksList = listOf<NetworkTrack>()
    private var dataBaseList: List<DataBaseTrack>? = null

    private val _trackListFromDataBase = MutableLiveData<List<RecyclerViewTrack>>()
    val dataBaseTrackList: LiveData<List<RecyclerViewTrack>>
        get() = _trackListFromDataBase

    private val _recyclerViewTrackList = MutableLiveData<List<RecyclerViewTrack>>()
    val recyclerViewTrackList: LiveData<List<RecyclerViewTrack>>
        get() = _recyclerViewTrackList

    private val _serverTrackList = MutableLiveData<List<SaveTrackRequest>>()
    val serverTrackList: LiveData<List<SaveTrackRequest>>
        get() = _serverTrackList

    private fun changeIsTrackOnServerToTrue(savedTrack: DataBaseSavedTrack) {
        compositeDisposable?.add(
            sqLiteRepository.changeIsTrackOnServerToTrue(savedTrack)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    private fun saveTrackToServer(query: String, saveTrackRequest: SaveTrackRequest) {
        networkRepository.saveTrack(query, saveTrackRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {})
    }

    private fun mapToNetworkTracks(list: List<DataBaseSavedTrack>): List<SaveTrackRequest> {
        val networkList = mutableListOf<SaveTrackRequest>()

        val filteredList = list.filter { it.isTrackOnServer == "false" }

        filteredList.forEachIndexed { index, dataBaseSavedTrack ->
            val pointsList = listOf(
                Point(
                    filteredList[index].startLongitude,
                    filteredList[index].startLatitude
                ), Point(
                    filteredList[index].finishLongitude,
                    filteredList[index].finishLatitude
                )
            )

            networkList.add(
                SaveTrackRequest(
                    sharedPreferences
                        .getString(SHARED_PREFERENCES_TOKEN_KEY, "")!!,
                    beginsAt = filteredList[index].startTime,
                    time = filteredList[index].joggingTime,
                    distance = filteredList[index].distance.toInt(),
                    points = pointsList
                )
            )

            changeIsTrackOnServerToTrue(dataBaseSavedTrack)
        }

        return networkList
    }

    fun getAllSavedTracksFromDataBase() {
        compositeDisposable?.add(
            sqLiteRepository.getAllSavedTracks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ dataBaseSavedTracksList ->
                    _serverTrackList.value = mapToNetworkTracks(dataBaseSavedTracksList)
                    serverTrackList.value?.forEach {
                        saveTrackToServer(
                            SAVE_TRACK_ON_SERVER_QUERY, SaveTrackRequest(
                                it.token, beginsAt = it.beginsAt, time = it.time,
                                distance = it.distance, points = it.points
                            )
                        )
                    }
                }, {})
        )
    }

    private fun insertTrack(dataBaseTrack: DataBaseTrack) {
        compositeDisposable?.add(
            sqLiteRepository.insertTrack(dataBaseTrack)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    fun getAllTracksFromServer(query: String, request: GetAllTracksRequest) {
        compositeDisposable?.add(
            networkRepository.getAllTracks(query, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ getAllTracksResponse ->
                    _recyclerViewTrackList.value = mapToRecyclerViewTrackList(getAllTracksResponse)
                    networkTracksList = getAllTracksResponse.tracks
                }, {}))
    }

    fun getAllTracksFromDataBase() {
        compositeDisposable?.add(
            sqLiteRepository.getAllTracks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _recyclerViewTrackList.value = mapToRecyclerViewTrackList(it)
                    _trackListFromDataBase.value = mapToRecyclerViewTrackList(it)
                    dataBaseList = it
                }, {}))
    }

    fun saveServerTracksToDataBase() {
        compositeDisposable?.add(
            Single.fromCallable {
                mapToDataBaseTrackList(networkTracksList)
                    .forEach { insertTrack(it) }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    private fun mapToRecyclerViewTrackList(response: GetAllTracksResponse):
            List<RecyclerViewTrack> {
        val trackList = mutableListOf<RecyclerViewTrack>()
        response.tracks.forEachIndexed { index, _ ->
            val simpleDateFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            val date = Date(response.tracks[index].beginsAt)

            val joggingTimeHundredOfMilliseconds =
                response.tracks[index].time / CONVERT_TO_HUNDRED_OF_MILLISECONDS_NUMBER %
                        CONVERT_TO_HUNDRED_OF_MILLISECONDS_NUMBER_SCALE

            val joggingTimeSeconds = response.tracks[index].time / CONVERT_TO_SECONDS_NUMBER %
                    CONVERT_TO_SECONDS_NUMBER_SCALE

            val joggingTimeMinutes = response.tracks[index].time / CONVERT_TO_MINUTES_NUMBER %
                    CONVERT_TO_MINUTES_NUMBER_SCALE

            val joggingTime = applicaiton.applicationContext
                .getString(R.string.layout_track_list_item_jogging_time_text)
                .format(joggingTimeMinutes, joggingTimeSeconds, joggingTimeHundredOfMilliseconds)

            trackList.add(
                RecyclerViewTrack(
                    simpleDateFormat.format(date),
                    response.tracks[index].distance.toString(),
                    joggingTime, startLongitude = dataBaseList?.get(index)?.startLongitude ?: 0.0,
                    startLatitude = dataBaseList?.get(index)?.startLatitude ?: 0.0,
                    finishLongitude = dataBaseList?.get(index)?.finishLongitude ?: 0.0,
                    finishLatitude = dataBaseList?.get(index)?.finishLatitude ?: 0.0))
        }

        return trackList
    }

    private fun mapToRecyclerViewTrackList(listOfDataBaseTracks: List<DataBaseTrack>):
            List<RecyclerViewTrack> {
        val recyclerViewTrackList = mutableListOf<RecyclerViewTrack>()

        listOfDataBaseTracks.forEachIndexed { index, _ ->

            val simpleDateFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            val date = Date(listOfDataBaseTracks[index].startTime)

            val joggingTimeHundredOfMilliseconds =
                listOfDataBaseTracks[index]
                    .joggingTime.toInt() / CONVERT_TO_HUNDRED_OF_MILLISECONDS_NUMBER %
                        CONVERT_TO_HUNDRED_OF_MILLISECONDS_NUMBER_SCALE

            val joggingTimeSeconds = listOfDataBaseTracks[index]
                .joggingTime.toInt() / CONVERT_TO_SECONDS_NUMBER % CONVERT_TO_SECONDS_NUMBER_SCALE

            val joggingTimeMinutes = listOfDataBaseTracks[index]
                .joggingTime.toInt() / CONVERT_TO_MINUTES_NUMBER % CONVERT_TO_MINUTES_NUMBER_SCALE

            val joggingTime = applicaiton.applicationContext
                .getString(R.string.layout_track_list_item_jogging_time_text)
                .format(joggingTimeMinutes, joggingTimeSeconds, joggingTimeHundredOfMilliseconds)

            recyclerViewTrackList.add(
                RecyclerViewTrack(
                    startTime = simpleDateFormat.format(date),
                    distance = listOfDataBaseTracks[index].distance,
                    joggingTime = joggingTime))
        }

        return recyclerViewTrackList
    }

    private fun mapToDataBaseTrackList(networkTracksList: List<NetworkTrack>):
            List<DataBaseTrack> {
        val dataBaseTrackList = mutableListOf<DataBaseTrack>()

        networkTracksList.forEachIndexed { index, _ ->
            dataBaseTrackList.add(
                DataBaseTrack(
                    startTime = networkTracksList[index].beginsAt,
                    distance = networkTracksList[index].distance.toString(),
                    joggingTime = networkTracksList[index].time.toLong()))
        }

        return dataBaseTrackList
    }

    fun synchronizeWithServer() {
        getAllTracksFromServer(
            GET_ALL_TRACKS_FROM_SERVER_QUERY, GetAllTracksRequest(
                sharedPreferences.getString(
                    SHARED_PREFERENCES_TOKEN_KEY,
                    ""
                ) ?: ""
            )
        )
        getAllSavedTracksFromDataBase()
    }

    override fun onCleared() {
        compositeDisposable?.clear()
        compositeDisposable = null

        super.onCleared()
    }
}