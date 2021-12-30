package com.senla.fitnessapp.data

import com.senla.fitnessapp.common.models.Notification
import com.senla.fitnessapp.data.database.SQLiteHelper
import com.senla.fitnessapp.data.network.RetrofitService
import com.senla.fitnessapp.data.network.models.LogInRequest
import com.senla.fitnessapp.data.network.models.LogInResponse
import com.senla.fitnessapp.data.network.models.RegisterRequest
import com.senla.fitnessapp.data.network.models.RegisterResponse
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class Repository @Inject constructor(
    private val retrofitService: RetrofitService
) {

    @Inject
    lateinit var sqLiteHelper: SQLiteHelper

    fun userLogIn(query: String, logInRequest: LogInRequest): Single<LogInResponse> =
        retrofitService.userLogIn(query, logInRequest)

    fun registerUser(query: String, registerRequest: RegisterRequest): Single<RegisterResponse> =
        retrofitService.registerUser(query, registerRequest)

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