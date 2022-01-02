package com.senla.fitnessapp.presentation.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.senla.fitnessapp.common.models.Notification
import com.senla.fitnessapp.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    val repository: Repository,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    companion object {
        private const val LOG_TAG = "SQLite"
        private const val LOG_DELETED_SUCCESSFULLY = "Notification was deleted"
        private const val LOG_DELETED_UNSUCCESSFULLY = "Error, the notification wasn't deleted"
    }

    private val _notificationList = MutableLiveData<ArrayList<Notification>>()
    val notificationList: LiveData<ArrayList<Notification>>
        get() = _notificationList

    fun getAllNotifications() {
        compositeDisposable.add(
            repository.getAllNotifications()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _notificationList.value = it }, {}))
    }

//
//    fun deleteNotificationById(id: Int) {
//        compositeDisposable.add(
//            repository.deleteNotificationById(id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    _notificationList.value?.remove()
//                    if (it > -1) Log.e(LOG_TAG, LOG_DELETED_SUCCESSFULLY)
//                    else Log.e(LOG_TAG, LOG_DELETED_UNSUCCESSFULLY)}, {})
//        )
//    }

    fun deleteNotificationById(notification: Notification) {
        compositeDisposable.add(
            repository.deleteNotificationById(notification.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    with(_notificationList) {
                        val list = value
                        value?.remove(notification)
                        value = list!!
                    }
                    if (it > -1) Log.e(LOG_TAG, LOG_DELETED_SUCCESSFULLY)
                    else Log.e(LOG_TAG, LOG_DELETED_UNSUCCESSFULLY)
                }, {}))
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}