package com.senla.fitnessapp.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment
import com.senla.fitnessapp.presentation.notification.recyclerView.NotificationAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment : Fragment(),
    NotificationAdapter.OnNotificationAdapterItemClickListener,
    NotificationDialogFragment.RefreshRecyclerView {

    companion object {
        const val NOTIFICATION_DIALOG_TAG = "notificationDialog"
        private const val PENDING_INTENT_REQUEST_CODE = 0
        private const val NOTIFICATION_TITLE = "Your Health"
        private const val NOTIFICATION_TEXT = "Пожалуйста, начните тренировку!" +
                " В здоровом теле здоровый дух!"
        const val JOGGING_FRAGMENT_EXTRA_KEY = "JoggingFragment"
    }

    private val CHANNEL_ID = "channelID"
    private val CHANNEL_NAME = "channelName"
    private val NOTIFICATION_ID = 0
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNavigationMenuButtons(
            binding.navView, navigateToFragment, R.id.menuItemMain,
            MainFragment(), sharedPreferences!!)
        setAddNotificationButton()
        initRecyclerView()
        createNotificationChannel()

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra(JOGGING_FRAGMENT_EXTRA_KEY, true)
        val pendingIntent = TaskStackBuilder.create(requireContext()).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(PENDING_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setSmallIcon(R.id.icon)
            .setContentText(NOTIFICATION_TEXT)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH).apply {
                    lightColor = Color.GREEN
                    enableLights(true)
            }
            val manager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            manager.createNotificationChannel(channel)
        }
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