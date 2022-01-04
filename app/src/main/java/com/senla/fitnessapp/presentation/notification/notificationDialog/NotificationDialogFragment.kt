package com.senla.fitnessapp.presentation.notification.notificationDialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.senla.fitnessapp.data.database.models.Notification
import com.senla.fitnessapp.databinding.FragmentNotificationDialogBinding
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
        private const val EDITTEXT_IS_EMPTY_WARNING = "Пожалуйста, заполните даннные."
        private const val CREATE_NOTIFICATION_LABEL = "Создать"
        private const val SAVE_NOTIFICATION_LABEL = "Сохранить"
        var savedDay = 0
        var savedMonth = 0
        var savedYear = 0
        var savedHour = 0
        var savedMinute = 0
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

    private fun setCreateNotificationButtonListener() {
        with(binding) {
            btnCreateNotification.setOnClickListener {
                if (notificationId == null) {
                    if (validationDone()) {
                        viewModel.createNotification(binding)
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
        return (binding.etNotificationText.text.toString().isNotEmpty() && savedDay != 0 &&
                savedYear != 0 && savedMinute != 0 && savedHour != 0)
    }

    private fun setAllButtonsListeners() {
        setChooseDayButtonListener()
        setChooseTimeButtonListener()
        setCreateNotificationButtonListener()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
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