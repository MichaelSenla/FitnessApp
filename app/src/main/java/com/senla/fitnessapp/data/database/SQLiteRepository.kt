package com.senla.fitnessapp.data.database

import com.senla.fitnessapp.data.database.models.DataBaseSavedTrack
import com.senla.fitnessapp.data.database.models.DataBaseTrack
import com.senla.fitnessapp.data.database.models.Notification
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SQLiteRepository @Inject constructor(private val sqLiteHelper: SQLiteHelper) {

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

    fun insertTrack(dataBaseTrack: DataBaseTrack): Single<Long> =
        sqLiteHelper.insertTrack(dataBaseTrack)

    fun insertSavedTrack(dataBaseSavedTrack: DataBaseSavedTrack): Single<Long> =
        sqLiteHelper.insertSavedTrack(dataBaseSavedTrack)

    fun getTrackById(id: Int): Single<DataBaseTrack>? = sqLiteHelper.getTrackById(id)

    fun getAllTracks(): Single<ArrayList<DataBaseTrack>> = sqLiteHelper.getAllTracks()

    fun getAllSavedTracks(): Single<List<DataBaseSavedTrack>> = sqLiteHelper.getAllSavedTracks()

    fun changeIsTrackOnServerToTrue(savedTrack: DataBaseSavedTrack): Single<Int> =
        sqLiteHelper.changeIsTrackOnServerToTrue(savedTrack)
}