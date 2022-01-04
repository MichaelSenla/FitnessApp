package com.senla.fitnessapp.presentation.notification.notificationDialog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.senla.fitnessapp.data.database.models.Notification
import com.senla.fitnessapp.data.database.SQLiteRepository
import com.senla.fitnessapp.databinding.FragmentNotificationDialogBinding
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedDay
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedHour
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedMinute
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedMonth
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedYear
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class NotificationDialogViewModel @Inject constructor(
    val sqLiteRepository: SQLiteRepository,
    val compositeDisposable: CompositeDisposable
) : ViewModel() {

    private val _notification = MutableLiveData<Notification>()
    val notification: LiveData<Notification>
        get() = _notification

    companion object {
        private const val SQLITE_LOG_TAG = "SQLite"
        private const val LOG_UPDATE_NOTIFICATION_ERROR = "SQLite"
    }

    fun createNotification(titleBinding: FragmentNotificationDialogBinding) {
        compositeDisposable.add(
            sqLiteRepository.insertNotification(
                Notification(title = titleBinding.etNotificationText.text.toString(),
                    time = StringBuilder("$savedDay/$savedMonth/$savedYear " +
                            "$savedHour:$savedMinute").toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ if (it <= -1) Log.e("SQLite", "Notification wasn't added") },
                    {}))
    }

    fun updateNotification(notificationId: Int?, binding: FragmentNotificationDialogBinding) {
        compositeDisposable.add(
            sqLiteRepository.updateNotification(
                Notification(id = notificationId!!,
                title = binding.etNotificationText.text.toString(),
                time = StringBuilder("$savedDay/$savedMonth/$savedYear " +
                        "$savedHour:$savedMinute").toString())
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({if (it <= -1)
                    Log.e(SQLITE_LOG_TAG, LOG_UPDATE_NOTIFICATION_ERROR)}, {}))
    }

    fun getNotificationById(id: Int) {
        compositeDisposable.add(sqLiteRepository.getNotificationById(id)
        !!.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({_notification.value = it}, {}))
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}