package com.senla.fitnessapp.presentation.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.senla.fitnessapp.R
import com.senla.fitnessapp.data.Repository
import com.senla.fitnessapp.databinding.FragmentNotificationBinding
import com.senla.fitnessapp.presentation.entry.EntryFragment
import com.senla.fitnessapp.presentation.main.MainFragment
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment
import com.senla.fitnessapp.presentation.notification.recyclerView.NotificationAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment: Fragment(),
    NotificationAdapter.OnNotificationAdapterItemClickListener {

    companion object {
        const val NOTIFICATION_DIALOG_TAG = "notificationDialog"
    }

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationViewModel by viewModels()
    @Inject
    lateinit var repository: Repository
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
        setSideNavigationToggleButton()
        setSideNavigationMenuButtons()
        setAddNotificationButton()
        initRecyclerView()
    }

    private fun setSideNavigationToggleButton() {
        toggle = ActionBarDrawerToggle(
            requireActivity(), binding.drawerLayout,
            R.string.fragment_main_open_navigation_label,
            R.string.fragment_main_close_navigation_label
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setSideNavigationMenuButtons() {
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuItemMain -> navigateToFragment(MainFragment())
                R.id.menuItemExit -> navigateToFragment(EntryFragment())
            }
            true
        }
    }

    private fun setAddNotificationButton() {
        binding.fab.setOnClickListener {
            NotificationDialogFragment(null).show(requireActivity().supportFragmentManager,
                NOTIFICATION_DIALOG_TAG)
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment).commit()
    }

    private fun initRecyclerView() = with(binding) {
        adapter = NotificationAdapter(this@NotificationFragment)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.submitList(repository.getAllNotifications())
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    override fun deleteItem() {

    }

    override fun changeItem(position: Int, id: Int) {
        NotificationDialogFragment(id).show(requireActivity().supportFragmentManager,
            NOTIFICATION_DIALOG_TAG)
    }
}