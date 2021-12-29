package com.senla.fitnessapp.presentation.notification.notificationDialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.senla.fitnessapp.data.Repository
import com.senla.fitnessapp.common.models.Notification
import com.senla.fitnessapp.databinding.FragmentNotificationDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDialogFragment(private val notificationId: Int?,
                                 private val listener: RefreshRecyclerView):
    DialogFragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    companion object {
        private const val EDITTEXT_IS_EMPTY_WARNING = "Пожалуйста, заполните даннные."
        private const val CREATE_NOTIFICATION_LABEL = "Создать"
        private const val SAVE_NOTIFICATION_LABEL = "Сохранить"
    }

    private var _binding: FragmentNotificationDialogBinding? = null
    private val binding get() = _binding!!
    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0
    @Inject
    lateinit var repository: Repository
    private lateinit var viewModel: NotificationDialogViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationDialogBinding
            .inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setTextToEditText()
        setAllButtonsListeners()
    }

    private fun setTextToEditText() {
        if (notificationId != null) {
            binding.btnCreateNotification.text = SAVE_NOTIFICATION_LABEL
            binding.etNotificationText
                .setText(repository.getNotificationById(notificationId)?.title)
        } else {
            binding.btnCreateNotification.text = CREATE_NOTIFICATION_LABEL
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
                        createNotification()
                    } else {
                        Toast.makeText(
                            requireContext(), EDITTEXT_IS_EMPTY_WARNING,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    updateNotification()
                }
                listener.refreshRecyclerView()
            }
        }
    }

    private fun createNotification() {
        val status = repository.insertNotification(
            Notification(
                title = binding.etNotificationText.text.toString(),
                time = StringBuilder("$savedDay/$savedMonth/$savedYear " +
                        "$savedHour:$savedMinute").toString()))

        if (status > -1) {
            Log.e("SQLite", "Notification was added!")
        } else {
            Log.e("SQLite", "Notification wasn't added")
        }

        this.dismiss()
    }

    private fun updateNotification() {
        val status = repository.updateNotification(Notification(id = notificationId!!,
            title = binding.etNotificationText.text.toString(),
            time = StringBuilder("$savedDay/$savedMonth/$savedYear " +
                    "$savedHour:$savedMinute").toString()))

        if (status > -1) {
            Log.e("SQLite", "Notification was updated!")
        } else {
            Log.e("SQLite", "Notification wasn't updated")
        }

        this.dismiss()
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