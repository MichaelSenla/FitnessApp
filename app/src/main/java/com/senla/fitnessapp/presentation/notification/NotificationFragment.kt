package com.senla.fitnessapp.presentation.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.senla.fitnessapp.R
import com.senla.fitnessapp.common.MainActivity
import com.senla.fitnessapp.data.database.models.Notification
import com.senla.fitnessapp.databinding.FragmentNotificationBinding
import com.senla.fitnessapp.presentation.jogging.JoggingFragment
import com.senla.fitnessapp.presentation.main.MainFragment
import com.senla.fitnessapp.presentation.navigation.SideNavigation.Companion.setNavigationMenuButtons
import com.senla.fitnessapp.presentation.notification.broadcast.NotificationReceiver
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedDay
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedHour
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedMinute
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedMonth
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.savedYear
import com.senla.fitnessapp.presentation.notification.recyclerView.NotificationAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class NotificationFragment : Fragment(),
    NotificationAdapter.OnNotificationAdapterItemClickListener,
    NotificationDialogFragment.RefreshRecyclerView {

    companion object {
        const val NOTIFICATION_DIALOG_TAG = "notificationDialog"
    }

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationViewModel by viewModels()
    private var adapter: NotificationAdapter? = null
    @set:Inject
    var sharedPreferences: SharedPreferences? = null
    private var updateAdapterObserver: Observer<ArrayList<Notification>>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNavigationMenuButtons(
            binding.navView, navigateToFragment, R.id.menuItemMain,
            MainFragment(), sharedPreferences!!)
        setAddNotificationButton()
        initRecyclerView()
    }

    private fun setAddNotificationButton() {
        binding.fab.setOnClickListener {
            NotificationDialogFragment(null, this)
                .show(requireActivity().supportFragmentManager,
                    NOTIFICATION_DIALOG_TAG)
        }
    }

    private val navigateToFragment: (Fragment) -> Unit = { fragment ->
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment).commit()
    }

    private fun initRecyclerView() = with(binding) {
        adapter = NotificationAdapter()
        adapter?.listener = this@NotificationFragment
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        viewModel.getAllNotifications()
    }

    override fun deleteItem(notification: Notification) {
        viewModel.deleteNotificationById(notification)
    }

    override fun changeItem(position: Int, id: Int) {
        NotificationDialogFragment(id, this).show(
            requireActivity().supportFragmentManager,
            NOTIFICATION_DIALOG_TAG)
    }

    override fun refreshRecyclerView() {
        viewModel.getAllNotifications()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    override fun onStart() {
        updateAdapterObserver = Observer { adapter?.submitList(it) }
        viewModel.notificationList.observe(this@NotificationFragment, updateAdapterObserver!!)

        super.onStart()
    }

    override fun onStop() {
        viewModel.notificationList.removeObserver(updateAdapterObserver!!)

        super.onStop()
    }
}