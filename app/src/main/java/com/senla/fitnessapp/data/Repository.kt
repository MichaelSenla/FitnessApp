package com.senla.fitnessapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.senla.fitnessapp.data.network.RetrofitService
import com.senla.fitnessapp.data.network.models.LogInResponse
import com.senla.fitnessapp.data.network.models.RegisterResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class Repository @Inject constructor() {

    @Inject
    lateinit var retrofitService: RetrofitService
    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    fun userLogIn(email: String, password: String): LiveData<LogInResponse> {
        val logInResponse = MutableLiveData<LogInResponse>()
        compositeDisposable.add(retrofitService.userLogIn(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                logInResponse.value = it
            })

        return logInResponse
    }

    fun registerUser(
        email: String, firstName: String,
        lastName: String, password: String
    ): LiveData<RegisterResponse> {
        val registerResponse = MutableLiveData<RegisterResponse>()
        compositeDisposable.add(retrofitService.registerUser(email, firstName, lastName, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                registerResponse.value = it
            })

        return registerResponse
    }

}