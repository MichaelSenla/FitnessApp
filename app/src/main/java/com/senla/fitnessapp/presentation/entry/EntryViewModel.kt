package com.senla.fitnessapp.presentation.entry

import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.senla.fitnessapp.data.network.NetworkRepository
import com.senla.fitnessapp.data.network.models.LogInRequest
import com.senla.fitnessapp.data.network.models.LogInResponse
import com.senla.fitnessapp.data.network.models.RegisterRequest
import com.senla.fitnessapp.data.network.models.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val compositeDisposable: CompositeDisposable,
    private val networkRepository: NetworkRepository
): ViewModel() {

    private val _logInResponse = MutableLiveData<LogInResponse>()
    val logInResponse: LiveData<LogInResponse>
        get() = _logInResponse

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse>
        get() = _registerResponse

    fun createUnderlineSpannable(text: String, startIndex: Int, endIndex: Int): SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            UnderlineSpan(), startIndex,
            endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        return spannableString
    }

    fun registerUser(query: String, registerRequest: RegisterRequest) {
            compositeDisposable.add(
                    networkRepository.registerUser(query, registerRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({_registerResponse.value = it}, {}))
    }

    fun userLogIn(query: String, logInRequest: LogInRequest) {
        compositeDisposable.add(
            networkRepository.userLogIn(query, logInRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({_logInResponse.value = it}, {}))
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}