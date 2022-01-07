package com.senla.fitnessapp.presentation.main

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.senla.fitnessapp.R
import com.senla.fitnessapp.common.Constants.SHARED_PREFERENCES_TOKEN_KEY
import com.senla.fitnessapp.data.database.models.DataBaseTrack
import com.senla.fitnessapp.data.network.models.getAllTracks.GetAllTracksRequest
import com.senla.fitnessapp.databinding.FragmentMainBinding
import com.senla.fitnessapp.presentation.entry.EntryFragment.Companion.FIRST_APP_USE_EXTRA_KEY
import com.senla.fitnessapp.presentation.jogging.JoggingFragment
import com.senla.fitnessapp.presentation.main.models.RecyclerViewTrack
import com.senla.fitnessapp.presentation.main.recyclerView.TrackAdapter
import com.senla.fitnessapp.presentation.navigation.SideNavigation.Companion.setNavigationMenuButtons
import com.senla.fitnessapp.presentation.notification.NotificationFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        private const val GET_ALL_TRACKS_FROM_SERVER_QUERY = "tracks"
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private var trackAdapter: TrackAdapter? = null
    private var dataBaseTrackListObserver: Observer<ArrayList<DataBaseTrack>>? = null
    private var networkTrackListObserver: Observer<ArrayList<RecyclerViewTrack>>? = null

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
        setNavigationMenuButtons(
            binding.navView, navigateToFragment, R.id.menuItemNotification,
            NotificationFragment(), sharedPreferences!!)

        initRecyclerView()

        if (arguments?.getBoolean(FIRST_APP_USE_EXTRA_KEY) == true) {
            binding.progressBar.isVisible = true
            viewModel.getAllTracksFromServer(
                GET_ALL_TRACKS_FROM_SERVER_QUERY, GetAllTracksRequest(
                    sharedPreferences!!.getString(
                        SHARED_PREFERENCES_TOKEN_KEY, "") ?: ""))
        } else {
            viewModel.getAllTracksFromDataBase()
        }
    }

    private fun initRecyclerView() {
        trackAdapter = TrackAdapter()
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun setMainFragmentListeners() {
        binding.fab.setOnClickListener {
            navigateToFragment(JoggingFragment())
        }
    }

    private fun pullToRefresh() {
        binding.pullToRefreshLayout.setOnRefreshListener {

        }
    }

    private val navigateToFragment: (Fragment) -> Unit = { fragment ->
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()

        networkTrackListObserver = Observer {
            trackAdapter?.submitList(it)
            binding.progressBar.isVisible = false
        }

        dataBaseTrackListObserver = Observer {
//            trackAdapter?.submitList(it)
        }
        viewModel.recyclerViewTrackList.observe(this, networkTrackListObserver!!)
    }
}