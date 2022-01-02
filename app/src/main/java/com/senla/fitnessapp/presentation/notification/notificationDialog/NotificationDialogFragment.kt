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
import com.senla.fitnessapp.common.models.Notification
import com.senla.fitnessapp.databinding.FragmentNotificationDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NotificationDialogFragment(private val notificationId: Int?,
                                 private val listener: RefreshRecyclerView):
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
    private lateinit var getNotificationObserver: Observer<Notification>

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
            setGetNotificationObserver()
            viewModel.notification.observe(this, getNotificationObserver)
            viewModel.getNotificationById(notificationId)
        } else {
            binding.btnCreateNotification.text = CREATE_NOTIFICATION_LABEL
        }
        setAllButtonsListeners()
    }

    private fun setGetNotificationObserver() {
        getNotificationObserver = Observer {
            binding.btnCreateNotification.text = SAVE_NOTIFICATION_LABEL
            binding.etNotificationText
                .setText(it.title)
        }
    }

    private fun setChooseDayButtonListener() {
        binding.btnChooseDay.setOnClickListener {
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val month = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val year = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

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
                    if (etNotificationText.text.toString().isNotEmpty()) {
                        viewModel.createNotification(binding)
                        this@NotificationDialogFragment.dismiss()
                    } else {
                        Toast.makeText(requireContext(), EDITTEXT_IS_EMPTY_WARNING,
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    viewModel.updateNotification(notificationId, binding)
                    this@NotificationDialogFragment.dismiss()
                }
                listener.refreshRecyclerView()
            }
        }
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

    interface RefreshRecyclerView {
        fun refreshRecyclerView()
    }
}