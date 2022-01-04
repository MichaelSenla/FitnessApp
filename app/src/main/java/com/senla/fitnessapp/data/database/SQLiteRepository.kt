package com.senla.fitnessapp.data.database

import com.senla.fitnessapp.data.database.models.Notification
import com.senla.fitnessapp.data.database.SQLiteHelper
import com.senla.fitnessapp.data.database.models.Track
import com.senla.fitnessapp.data.network.RetrofitService
import com.senla.fitnessapp.data.network.models.*
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SQLiteRepository {

    @Inject
    lateinit var sqLiteHelper: SQLiteHelper

    fun insertNotification(notification: Notification): Single<Long> {
        return sqLiteHelper.insertNotification(notification)
    }

    fun getAllNotifications(): Single<ArrayList<Notification>> {
        return sqLiteHelper.getAllNotifications()
    }

    fun getNotificationById(id: Int): Single<Notification>? {
        return sqLiteHelper.getNotificationById(id)
    }

    fun deleteNotificationById(id: Int): Single<Int> {
        return sqLiteHelper.deleteNotificationById(id)
    }

    fun updateNotification(notification: Notification): Single<Int> {
        return sqLiteHelper.updateNotification(notification)
    }

    fun insertTrack(track: Track): Single<Long> {
        return sqLiteHelper.insertTrack(track)
    }

    fun getTrackById(id: Int): Single<Track>? {
        return sqLiteHelper.getTrackById(id)
    }
}