package com.senla.fitnessapp.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.senla.fitnessapp.data.database.SQLiteHelper
import com.senla.fitnessapp.data.database.models.DataBaseTrack
import com.senla.fitnessapp.data.network.NetworkRepository
import com.senla.fitnessapp.data.network.models.getAllTracks.GetAllTracksResponse
import com.senla.fitnessapp.presentation.main.models.RecyclerViewTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val networkRepository: NetworkRepository,
    val sqLiteHelper: SQLiteHelper,
    val compositeDisposable: CompositeDisposable
) : ViewModel() {

    private val _trackListFromDataBase = MutableLiveData<ArrayList<DataBaseTrack>>()
    val dataBaseTrackListFromDataBase: LiveData<ArrayList<DataBaseTrack>>
        get() = _trackListFromDataBase

    private val _recyclerViewTrackList = MutableLiveData<ArrayList<RecyclerViewTrack>>()
    val recyclerViewTrackList: LiveData<ArrayList<RecyclerViewTrack>>
        get() = _recyclerViewTrackList

    fun getAllTracksFromServer(query: String, token: String) {
        compositeDisposable.add(
            networkRepository.getAllTracks(query, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _recyclerViewTrackList.value = mapToRecyclerViewTrackList(it) }, {})
        )
    }

    fun getAllTracksFromDataBase() {
        compositeDisposable.add(
            sqLiteHelper.getAllTracks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _trackListFromDataBase.value = it }, {})
        )
    }

    private fun mapToRecyclerViewTrackList(response: GetAllTracksResponse):
            ArrayList<RecyclerViewTrack> {
        val trackList = arrayListOf<RecyclerViewTrack>()
        response.networkTracks.forEachIndexed { index, value ->
            trackList.add(
                RecyclerViewTrack(
                    response.networkTracks[index].beginsAt.toString(),
                    response.networkTracks[index].distance.toString(),
                    response.networkTracks[index].time)
            )
        }

        return trackList
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}