package com.senla.fitnessapp.presentation.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.viewModels
import com.senla.fitnessapp.R
import com.senla.fitnessapp.databinding.FragmentMainBinding
import com.senla.fitnessapp.presentation.entry.EntryFragment
import com.senla.fitnessapp.presentation.jogging.JoggingFragment
import com.senla.fitnessapp.presentation.notification.NotificationFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment(): Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setMainFragmentListeners()
        setNavigationToggleButton()
        setNavigationMenuButtons()
    }

    private fun navigateToJoggingFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, JoggingFragment()).commit()
    }

    private fun setMainFragmentListeners() {
        binding.fab.setOnClickListener {
            navigateToJoggingFragment()
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}