package com.senla.fitnessapp.presentation.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.senla.fitnessapp.R
import com.senla.fitnessapp.databinding.FragmentTrackBinding
import com.senla.fitnessapp.presentation.entry.EntryFragment
import com.senla.fitnessapp.presentation.main.MainFragment
import com.senla.fitnessapp.presentation.notification.NotificationFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackFragment: Fragment() {

    private var _binding: FragmentTrackBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrackViewModel by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNavigationToggleButton()
        setNavigationMenuButtons()
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
                R.id.menuItemNotification -> navigateToFragment(NotificationFragment())
                R.id.menuItemExit -> navigateToFragment(EntryFragment())
            }
            true
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment).commit()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}