package com.senla.fitnessapp.presentation.jogging

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.senla.fitnessapp.data.database.SQLiteRepository
import com.senla.fitnessapp.data.database.models.DataBaseSavedTrack
import com.senla.fitnessapp.data.database.models.DataBaseTrack
import com.senla.fitnessapp.data.network.NetworkRepository
import com.senla.fitnessapp.data.network.models.saveTrack.SaveTrackResponse
import com.senla.fitnessapp.data.network.models.saveTrack.saveTrackRequest.SaveTrackRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class JoggingViewModel @Inject constructor(
    val sqLiteRepository: SQLiteRepository,
    val networkRepository: NetworkRepository,
    val compositeDisposable: CompositeDisposable
) : ViewModel() {

    companion object {
        private const val ONE_MINUTE_NUMBER = 600
        private const val MINUTES_SCALE_NUMBER = 60
        private const val ONE_SECOND_NUMBER = 10
        private const val SECONDS_SCALE_NUMBER = 60
        private const val ONE_HUNDRED_MILLISECONDS_SCALE_NUMBER = 10
    }

    private val _track = MutableLiveData<DataBaseTrack>()
    val dataBaseTrack: LiveData<DataBaseTrack>
        get() = _track

    private val _saveTrackResponse = MutableLiveData<SaveTrackResponse>()
    private val saveTrackResponse: LiveData<SaveTrackResponse>
        get() = _saveTrackResponse

    fun insertTrack(dataBaseTrack: DataBaseTrack) {
        compositeDisposable.add(
            sqLiteRepository.insertTrack(dataBaseTrack)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    fun insertSavedTrack(dataBaseSavedTrack: DataBaseSavedTrack) {
        compositeDisposable.add(
            sqLiteRepository.insertSavedTrack(dataBaseSavedTrack)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    fun saveTrack(query: String, saveTrackRequest: SaveTrackRequest) {
        compositeDisposable.add(
            networkRepository.saveTrack(query, saveTrackRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _saveTrackResponse.value = it }, {})
        )
    }

    fun getTrackById(id: Int) {
        compositeDisposable.add(
            sqLiteRepository.getTrackById(id)
            !!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _track.value = it }, {})
        )
    }

    fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()

        val minutes = resultInt / ONE_MINUTE_NUMBER % MINUTES_SCALE_NUMBER
        val seconds = resultInt / ONE_SECOND_NUMBER % SECONDS_SCALE_NUMBER
        val milliseconds = resultInt % ONE_HUNDRED_MILLISECONDS_SCALE_NUMBER

        return makeTimeString(minutes, seconds, milliseconds)
    }

    private fun makeTimeString(minutes: Int, seconds: Int, milliseconds: Int): String =
        String.format("%02d:%02d:%02d", minutes, seconds, milliseconds)

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}