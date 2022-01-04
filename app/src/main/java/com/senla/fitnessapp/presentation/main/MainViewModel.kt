package com.senla.fitnessapp.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.senla.fitnessapp.data.database.SQLiteHelper
import com.senla.fitnessapp.data.database.models.Track
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val sqLiteHelper: SQLiteHelper,
    val compositeDisposable: CompositeDisposable
) : ViewModel() {

    private val _trackList = MutableLiveData<ArrayList<Track>>()
    val trackList: LiveData<ArrayList<Track>>
        get() = _trackList

    fun getAllTracks() {
        compositeDisposable.add(
            sqLiteHelper.getAllTracks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _trackList.value = it }, {}))
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}