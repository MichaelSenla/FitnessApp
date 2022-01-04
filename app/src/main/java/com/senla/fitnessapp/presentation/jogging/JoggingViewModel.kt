package com.senla.fitnessapp.presentation.jogging

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.senla.fitnessapp.data.database.SQLiteRepository
import com.senla.fitnessapp.data.database.models.Track
import com.senla.fitnessapp.data.network.NetworkRepository
import com.senla.fitnessapp.data.network.models.saveTrackRequest.SaveTrackRequest
import com.senla.fitnessapp.data.network.models.SaveTrackResponse
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

    private val _track = MutableLiveData<Track>()
    val track: LiveData<Track>
        get() = _track

    private val _saveTrackResponse = MutableLiveData<SaveTrackResponse>()
    private val saveTrackResponse: LiveData<SaveTrackResponse>
        get() = _saveTrackResponse

    private val _trackWasInserted = MutableLiveData<Boolean>()
    private val trackWasInserted: LiveData<Boolean>
        get() = _trackWasInserted


    fun saveTrack(query: String, saveTrackRequest: SaveTrackRequest) {
        compositeDisposable.add(
            networkRepository.saveTrack(query, saveTrackRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _saveTrackResponse.value = it }, {})
        )
    }

    fun insertTrack(track: Track) {
        compositeDisposable.add(
            sqLiteRepository.insertTrack(track).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ if (it > -1) _trackWasInserted.value = true }, {})
        )
    }

    fun getTrackById(id: Int) {
        compositeDisposable.add(
            sqLiteRepository.getTrackById(id)
            !!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _track.value = it }, {}))
    }

    fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()

        val minutes = resultInt % 86400 % 36000 / 600 //100 = 10
        val seconds = resultInt % 86400 % 3600 % 600 / 10
        val milliseconds = resultInt % 86400 % 3600 % 60 % 10

        return makeTimeString(minutes, seconds, milliseconds)
    }

    private fun makeTimeString(minutes: Int, seconds: Int, milliseconds: Int): String =
        String.format("%02d:%02d:%02d", minutes, seconds, milliseconds)

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}