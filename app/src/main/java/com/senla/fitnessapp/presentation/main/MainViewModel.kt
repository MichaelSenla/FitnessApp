package com.senla.fitnessapp.presentation.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.senla.fitnessapp.R
import com.senla.fitnessapp.data.database.SQLiteRepository
import com.senla.fitnessapp.data.database.models.DataBaseTrack
import com.senla.fitnessapp.data.network.NetworkRepository
import com.senla.fitnessapp.data.network.models.getAllTracks.GetAllTracksRequest
import com.senla.fitnessapp.data.network.models.getAllTracks.getAllTracksResponse.GetAllTracksResponse
import com.senla.fitnessapp.presentation.main.models.RecyclerViewTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val applicaiton: Application,
    val networkRepository: NetworkRepository,
    val sqLiteRepository: SQLiteRepository
) : AndroidViewModel(applicaiton) {

    companion object {
        private const val TIME_FORMAT = "dd.MM.yyyy HH:mm:ss"
        private const val CONVERT_TO_HUNDRED_OF_MILLISECONDS_NUMBER = 100
        private const val CONVERT_TO_SECONDS_NUMBER = 1000
        private const val CONVERT_TO_MINUTES_NUMBER = 60_000
        private const val CONVERT_TO_HUNDRED_OF_MILLISECONDS_NUMBER_SCALE = 10
        private const val CONVERT_TO_SECONDS_NUMBER_SCALE = 60
        private const val CONVERT_TO_MINUTES_NUMBER_SCALE = 60
    }

    private var compositeDisposable: CompositeDisposable? = CompositeDisposable()

    private val _trackListFromDataBase = MutableLiveData<ArrayList<DataBaseTrack>>()
    val dataBaseTrackListFromDataBase: LiveData<ArrayList<DataBaseTrack>>
        get() = _trackListFromDataBase

    private val _recyclerViewTrackList = MutableLiveData<ArrayList<RecyclerViewTrack>>()
    val recyclerViewTrackList: LiveData<ArrayList<RecyclerViewTrack>>
        get() = _recyclerViewTrackList

    fun insertTrack(dataBaseTrack: DataBaseTrack) {
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
                    mapToDataBaseTrackList(mapToRecyclerViewTrackList(getAllTracksResponse))
                        .forEach { insertTrack(it) }}, {}))
    }

    fun getAllTracksFromDataBase() {
        compositeDisposable?.add(
            sqLiteRepository.getAllTracks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _trackListFromDataBase.value = it }, {})
        )
    }

    private fun mapToRecyclerViewTrackList(response: GetAllTracksResponse):
            ArrayList<RecyclerViewTrack> {
        val trackList = arrayListOf<RecyclerViewTrack>()
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
                    joggingTime
                )
            )
        }

        return trackList
    }

//    private fun mapToRecyclerViewTrackList(response: DataBaseTrack): List<RecyclerViewTrack> {
//
//    }

    private fun mapToDataBaseTrackList(recyclerViewTrackList: List<RecyclerViewTrack>):
            List<DataBaseTrack> {
        val dataBaseTrackList = mutableListOf<DataBaseTrack>()

        recyclerViewTrackList.forEachIndexed { index, _ ->
            dataBaseTrackList.add(
                DataBaseTrack(
                    startTime = recyclerViewTrackList[index].startTime,
                    distance = recyclerViewTrackList[index].distance,
                    joggingTime = recyclerViewTrackList[index].joggingTime
                )
            )
        }

        return dataBaseTrackList
    }

    override fun onCleared() {
        compositeDisposable?.clear()
        compositeDisposable = null

        super.onCleared()
    }
}