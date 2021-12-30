package com.senla.fitnessapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.senla.fitnessapp.common.models.Notification
import com.senla.fitnessapp.data.database.SQLiteHelper
import com.senla.fitnessapp.data.network.RetrofitService
import com.senla.fitnessapp.data.network.models.LogInRequest
import com.senla.fitnessapp.data.network.models.LogInResponse
import com.senla.fitnessapp.data.network.models.RegisterRequest
import com.senla.fitnessapp.data.network.models.RegisterResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class Repository @Inject constructor(
    private val retrofitService: RetrofitService,
    private val compositeDisposable: CompositeDisposable
) {

    @Inject
    lateinit var sqLiteHelper: SQLiteHelper

    private val _logInResponse = MutableLiveData<LogInResponse>()
    val logInResponse: LiveData<LogInResponse>
        get() = _logInResponse

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse>
        get() = _registerResponse

    fun userLogIn(query: String, logInRequest: LogInRequest): LiveData<LogInResponse> {
        compositeDisposable.add(
            retrofitService.userLogIn(query, logInRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    val responseValue = response
                    _logInResponse.value = responseValue
                           }, {
                    Log.e("checking", "${it.message}")
                })
        )
        Log.e("checking", "${_logInResponse.value}")

        return logInResponse
    }

    fun registerUser(query: String, registerRequest: RegisterRequest): LiveData<RegisterResponse> {
        compositeDisposable.add(
            retrofitService.registerUser(query, registerRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({_registerResponse.value = it}, {})
        )

        Log.e("checking", "${_registerResponse.value?.status}")
        Log.e("checking", "${registerResponse.value?.status}")
        return registerResponse
    }

    fun insertNotification(notification: Notification): Long {
        return sqLiteHelper.insertNotification(notification)
    }

    fun getAllNotifications(): ArrayList<Notification> {
        return sqLiteHelper.getAllNotifications()
    }

    fun getNotificationById(id: Int): Notification? {
        return sqLiteHelper.getNotificationById(id)
    }

    fun deleteNotificationById(id: Int): Int {
        return sqLiteHelper.deleteNotificationById(id)
    }

    fun updateNotification(notification: Notification): Int {
        return sqLiteHelper.updateNotification(notification)
    }
}