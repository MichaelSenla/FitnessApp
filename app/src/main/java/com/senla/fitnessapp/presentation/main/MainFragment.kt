package com.senla.fitnessapp.presentation.main

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.senla.fitnessapp.R
import com.senla.fitnessapp.common.Constants.SHARED_PREFERENCES_TOKEN_KEY
import com.senla.fitnessapp.data.database.models.Track
import com.senla.fitnessapp.databinding.FragmentMainBinding
import com.senla.fitnessapp.presentation.entry.EntryFragment
import com.senla.fitnessapp.presentation.jogging.JoggingFragment
import com.senla.fitnessapp.presentation.main.recyclerView.TrackAdapter
import com.senla.fitnessapp.presentation.navigation.SideNavigation.Companion.setMenuExitButton
import com.senla.fitnessapp.presentation.navigation.SideNavigation.Companion.setNavigationMenuButtons
import com.senla.fitnessapp.presentation.notification.NotificationFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private var adapter: TrackAdapter? = null
    private var trackListObserver: Observer<ArrayList<Track>>? = null
    @set:Inject
    var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setMainFragmentListeners()
        setNavigationMenuButtons(binding.navView, navigateToFragment, R.id.menuItemNotification,
            NotificationFragment(), sharedPreferences!!)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = TrackAdapter()
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapter
        }
    }

    private fun setMainFragmentListeners() {
        binding.fab.setOnClickListener {
            navigateToFragment(JoggingFragment())
        }
    }

    private val navigateToFragment: (Fragment) -> Unit = { fragment ->
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment).commit()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    override fun onStart() {
//        trackListObserver = Observer { adapter?.submitList(it) }
        super.onStart()
    }
}