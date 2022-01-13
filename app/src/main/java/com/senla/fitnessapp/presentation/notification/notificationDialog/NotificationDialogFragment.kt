package com.senla.fitnessapp.presentation.notification.notificationDialog

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.senla.fitnessapp.R
import com.senla.fitnessapp.data.database.models.Notification
import com.senla.fitnessapp.databinding.FragmentNotificationDialogBinding
import com.senla.fitnessapp.presentation.notification.broadcast.NotificationReceiver
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NotificationDialogFragment(
    private val notificationId: Int?,
    private val listener: RefreshRecyclerView
) :
    DialogFragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    companion object {
        const val PENDING_INTENT_REQUEST_CODE = 0
        private const val EDITTEXT_IS_EMPTY_WARNING = "Пожалуйста, заполните даннные."
        private const val CREATE_NOTIFICATION_LABEL = "Создать"
        private const val SAVE_NOTIFICATION_LABEL = "Сохранить"
        private const val IMPOSSIBLE_VALUE = 0
        var savedDay = 0
        var savedMonth: Int? = null
        var savedYear = 0
        var savedHour: Int? = null
        var savedMinute: Int? = null
    }

    private var _binding: FragmentNotificationDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationDialogViewModel by viewModels()
    private var getNotificationObserver: Observer<Notification>? = null
    private var notificationWasCreatedOrUpdatedObserver: Observer<Boolean>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationDialogBinding
            .inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (notificationId != null) {
            viewModel.getNotificationById(notificationId)
        } else {
            binding.btnCreateNotification.text = CREATE_NOTIFICATION_LABEL
        }
        setAllButtonsListeners()
    }

    private fun getNotificationObserver(): Observer<Notification>? {
        getNotificationObserver = Observer {
            binding.btnCreateNotification.text = SAVE_NOTIFICATION_LABEL
            binding.etNotificationText
                .setText(it.title)
        }
        return getNotificationObserver
    }

    private fun setChooseDayButtonListener() {
        binding.btnChooseDay.setOnClickListener {
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val month = Calendar.getInstance().get(Calendar.MONTH)
            val year = Calendar.getInstance().get(Calendar.YEAR)

            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
    }

    private fun setChooseTimeButtonListener() {
        binding.btnChooseTime.setOnClickListener {
            val hour = Calendar.getInstance().get(Calendar.HOUR)
            val minute = Calendar.getInstance().get(Calendar.MINUTE)

            TimePickerDialog(requireContext(), this, hour, minute, true).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setCreateNotificationButtonListener() {
        with(binding) {
            btnCreateNotification.setOnClickListener {
                if (notificationId == null) {
                    if (validationDone()) {
                        viewModel.createNotification(binding)
                        setAlarmManager()
                    } else {
                        Toast.makeText(
                            requireContext(), EDITTEXT_IS_EMPTY_WARNING, Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (validationDone()) {
                        viewModel.updateNotification(notificationId, binding)
                    } else {
                        Toast.makeText(
                            requireContext(), EDITTEXT_IS_EMPTY_WARNING, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                listener.refreshRecyclerView()
            }
        }
    }

    private fun validationDone(): Boolean {
        return (binding.etNotificationText.text.toString()
            .isNotEmpty() && savedDay != IMPOSSIBLE_VALUE &&
                savedMonth != null && savedYear != IMPOSSIBLE_VALUE &&
                savedMinute != null && savedHour != null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAllButtonsListeners() {
        setChooseDayButtonListener()
        setChooseTimeButtonListener()
        setCreateNotificationButtonListener()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        binding.btnChooseDay.text =
            getString(R.string.fragment_notification_dialog_chosen_day_pattern_text)
                .format(savedDay, savedMonth, savedYear)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        binding.btnChooseTime.text =
            getString(R.string.fragment_notification_dialog_chosen_time_pattern_text)
                .format(savedHour, savedMinute)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarmManager() {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE
                    or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE)
                as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.set(savedYear, savedMonth!!, savedDay, savedHour!!, savedMinute!!)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis, pendingIntent
        )
        Log.e("TIME_CHECKING", "${calendar.timeInMillis}")
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    override fun onStart() {
        notificationWasCreatedOrUpdatedObserver =
            Observer { this@NotificationDialogFragment.dismiss() }
        viewModel.notification.observe(this, getNotificationObserver()!!)
        viewModel.notificationWasCreatedOrUpdated.observe(
            this, notificationWasCreatedOrUpdatedObserver!!
        )

        super.onStart()
    }

    override fun onStop() {
        viewModel.notification.removeObserver(getNotificationObserver!!)
        viewModel.notificationWasCreatedOrUpdated
            .removeObserver(notificationWasCreatedOrUpdatedObserver!!)

        super.onStop()
    }

    interface RefreshRecyclerView {
        fun refreshRecyclerView()
    }
}