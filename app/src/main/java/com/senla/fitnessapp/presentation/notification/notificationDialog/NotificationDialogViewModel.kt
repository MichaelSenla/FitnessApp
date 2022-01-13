package com.senla.fitnessapp.presentation.notification.notificationDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.senla.fitnessapp.data.database.SQLiteRepository
import com.senla.fitnessapp.data.database.models.Notification
import com.senla.fitnessapp.databinding.FragmentNotificationDialogBinding
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedDay
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedHour
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedMinute
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedMonth
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedYear
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class NotificationDialogViewModel @Inject constructor(
    val sqLiteRepository: SQLiteRepository,
    val compositeDisposable: CompositeDisposable
) : ViewModel() {

    companion object {
        private const val RESET_TIME_VALUE = 0
        private const val DATABASE_QUERY_FAILED_VALUE = -1
    }

    private val _notification = MutableLiveData<Notification>()
    val notification: LiveData<Notification>
        get() = _notification

    private val _notificationWasCreatedOrUpdated = MutableLiveData<Boolean>()
    val notificationWasCreatedOrUpdated: LiveData<Boolean>
        get() = _notificationWasCreatedOrUpdated

    private fun resetDateAndTimeValues() {
        savedDay = RESET_TIME_VALUE
        savedMonth = RESET_TIME_VALUE
        savedYear = RESET_TIME_VALUE
        savedMinute = RESET_TIME_VALUE
        savedHour = RESET_TIME_VALUE
    }

    fun createNotification(titleBinding: FragmentNotificationDialogBinding) {
        compositeDisposable.add(
            sqLiteRepository.insertNotification(
                Notification(
                    title = titleBinding.etNotificationText.text.toString(),
                    time = StringBuilder(
                        "$savedDay/$savedMonth/$savedYear " +
                                "$savedHour:$savedMinute").toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it > DATABASE_QUERY_FAILED_VALUE)
                        _notificationWasCreatedOrUpdated.value = true;
                    resetDateAndTimeValues()
                }, {}) ?: Disposable.empty()
        )
    }

    fun updateNotification(notificationId: Int?, binding: FragmentNotificationDialogBinding) {
        compositeDisposable.add(
            sqLiteRepository.updateNotification(
                Notification(
                    id = notificationId!!,
                    title = binding.etNotificationText.text.toString(),
                    time = StringBuilder(
                        "$savedDay/$savedMonth/$savedYear " +
                                "$savedHour:$savedMinute").toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it > DATABASE_QUERY_FAILED_VALUE)
                        _notificationWasCreatedOrUpdated.value = true;
                    resetDateAndTimeValues()
                }, {}) ?: Disposable.empty()
        )
    }

    fun getNotificationById(id: Int) {
        compositeDisposable.add(
            sqLiteRepository.getNotificationById(id)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ _notification.value = it }, {}) ?: Disposable.empty()
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()

        super.onCleared()
    }
}