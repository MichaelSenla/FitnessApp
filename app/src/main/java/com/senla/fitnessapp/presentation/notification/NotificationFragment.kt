package com.senla.fitnessapp.presentation.notification

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.senla.fitnessapp.R
import com.senla.fitnessapp.databinding.FragmentNotificationBinding
import com.senla.fitnessapp.presentation.entry.EntryFragment
import com.senla.fitnessapp.presentation.main.MainFragment
import com.senla.fitnessapp.presentation.notification.models.Notification
import com.senla.fitnessapp.presentation.notification.recyclerView.NotificationAdapter
import java.lang.StringBuilder
import java.time.Year
import java.util.*

class NotificationFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationViewModel by viewModels()
    private val listOfNotifications = mutableListOf<Notification>()
    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNavigationToggleButton()
        setNavigationMenuButtons()
        setAddNotificationButton()
        initRecyclerView()
    }

    private fun setNavigationToggleButton() {
        toggle = ActionBarDrawerToggle(requireActivity(), binding.drawerLayout,
            R.string.fragment_main_open_navigation_label,
            R.string.fragment_main_close_navigation_label)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setNavigationMenuButtons() {
        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.menuItemMain -> navigateToFragment(MainFragment())
                R.id.menuItemExit -> navigateToFragment(EntryFragment())
            }
            true
        }
    }

    private fun setAddNotificationButton() {
        binding.fab.setOnClickListener {
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val month = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val year = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
    }

    private fun getTimePickerDialog() {
        val hour = Calendar.getInstance().get(Calendar.HOUR)
        val minute = Calendar.getInstance().get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment).commit()
    }

    private fun initRecyclerView() = with(binding) {
        adapter = NotificationAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
       // adapter.submitList()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
        getTimePickerDialog()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        val stringBuilder = StringBuilder()
        stringBuilder
        //listOfNotifications.add(Notification())
    }
}