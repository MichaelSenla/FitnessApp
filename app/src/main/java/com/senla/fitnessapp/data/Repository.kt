package com.senla.fitnessapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.senla.fitnessapp.data.network.RetrofitService
import com.senla.fitnessapp.data.network.models.LogInResponse
import com.senla.fitnessapp.data.network.models.RegisterRequest
import com.senla.fitnessapp.data.network.models.RegisterResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class Repository @Inject constructor(private val retrofitService: RetrofitService,
                                     private val compositeDisposable: CompositeDisposable) {

    private val _logInResponse = MutableLiveData<LogInResponse>()
    val logInResponse: LiveData<LogInResponse>
        get() = _logInResponse

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse>
        get() = _registerResponse

    fun userLogIn(query: String, email: String, password: String): LiveData<LogInResponse> {
        compositeDisposable.add(
            retrofitService.userLogIn(query, email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _logInResponse.value = it}, {}))
        Log.e("checking", "${_logInResponse.value}")

        return logInResponse
    }

    fun registerUser(query: String, registerRequest: RegisterRequest): LiveData<RegisterResponse> {
        compositeDisposable.add(retrofitService.registerUser(query, registerRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({_registerResponse.postValue(it)}, {}))

        return registerResponse
    }
}