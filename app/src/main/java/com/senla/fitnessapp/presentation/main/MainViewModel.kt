package com.senla.fitnessapp.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.senla.fitnessapp.data.database.SQLiteHelper
import com.senla.fitnessapp.data.database.models.DataBaseTrack
import com.senla.fitnessapp.data.network.NetworkRepository
import com.senla.fitnessapp.data.network.models.getAllTracks.GetAllTracksRequest
import com.senla.fitnessapp.data.network.models.getAllTracks.getAllTracksResponse.GetAllTracksResponse
import com.senla.fitnessapp.presentation.main.models.RecyclerViewTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val networkRepository: NetworkRepository,
    val sqLiteHelper: SQLiteHelper) : ViewModel() {

    private var compositeDisposable: CompositeDisposable? = CompositeDisposable()

    private val _trackListFromDataBase = MutableLiveData<ArrayList<DataBaseTrack>>()
    val dataBaseTrackListFromDataBase: LiveData<ArrayList<DataBaseTrack>>
        get() = _trackListFromDataBase

    private val _recyclerViewTrackList = MutableLiveData<ArrayList<RecyclerViewTrack>>()
    val recyclerViewTrackList: LiveData<ArrayList<RecyclerViewTrack>>
        get() = _recyclerViewTrackList

    fun getAllTracksFromServer(query: String, request: GetAllTracksRequest) {
        compositeDisposable?.add(
            networkRepository.getAllTracks(query, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Log.e("TEST", "${it}")
                    _recyclerViewTrackList.value = mapToRecyclerViewTrackList(it)
                    Log.e("TEST", "${_recyclerViewTrackList.value}")}, {}))
    }

    fun getAllTracksFromDataBase() {
        compositeDisposable?.add(
            sqLiteHelper.getAllTracks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _trackListFromDataBase.value = it }, {}))
    }

    private fun mapToRecyclerViewTrackList(response: GetAllTracksResponse):
            ArrayList<RecyclerViewTrack> {
        val trackList = arrayListOf<RecyclerViewTrack>()
        response.tracks.forEachIndexed { index, _ ->
            trackList.add(RecyclerViewTrack(
                    response.tracks[index].beginsAt.toString(),
                    response.tracks[index].distance.toString(),
                    response.tracks[index].time))
        }

        return trackList
    }

    override fun onCleared() {
        compositeDisposable?.clear()
        compositeDisposable = null

        super.onCleared()
    }
}