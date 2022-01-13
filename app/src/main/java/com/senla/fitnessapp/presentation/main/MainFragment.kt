package com.senla.fitnessapp.presentation.main

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.senla.fitnessapp.R
import com.senla.fitnessapp.common.Constants.SHARED_PREFERENCES_TOKEN_KEY
import com.senla.fitnessapp.data.network.models.getAllTracks.GetAllTracksRequest
import com.senla.fitnessapp.databinding.FragmentMainBinding
import com.senla.fitnessapp.presentation.entry.EntryFragment
import com.senla.fitnessapp.presentation.entry.EntryFragment.Companion.FIRST_APP_USE_EXTRA_KEY
import com.senla.fitnessapp.presentation.jogging.JoggingFragment
import com.senla.fitnessapp.presentation.main.models.RecyclerViewTrack
import com.senla.fitnessapp.presentation.main.recyclerView.TrackAdapter
import com.senla.fitnessapp.presentation.navigation.SideNavigation.Companion.setMenuExitButton
import com.senla.fitnessapp.presentation.navigation.SideNavigation.Companion.setNavigationMenuButtons
import com.senla.fitnessapp.presentation.notification.NotificationFragment
import com.senla.fitnessapp.presentation.track.TrackFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(), TrackAdapter.OnTrackAdapterItemClick {

    companion object {
        const val GET_ALL_TRACKS_FROM_SERVER_QUERY = "tracks"
        const val LOCATION_COORDINATES_EXTRA_KEY = "Location coordinates"
        const val TRACK_JOGGING_TIME_EXTRA_KEY = "TRACK_START_TIME"
        const val TRACK_DISTANCE_EXTRA_KEY = "DISTANCE"
        const val SHARED_PREFERENCES_TOKEN_KEY_DEFAULT_VALUE = ""
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private var trackAdapter: TrackAdapter? = null
    private var dataBaseTrackListObserver: Observer<List<RecyclerViewTrack>>? = null
    private var networkTrackListObserver: Observer<List<RecyclerViewTrack>>? = null

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
            NotificationFragment()
        )

        initRecyclerView()

        if (arguments?.getBoolean(FIRST_APP_USE_EXTRA_KEY) == true) {
            binding.progressBar.isVisible = true
            viewModel.getAllTracksFromServer(
                GET_ALL_TRACKS_FROM_SERVER_QUERY, GetAllTracksRequest(
                    sharedPreferences!!.getString(
                        SHARED_PREFERENCES_TOKEN_KEY,
                        SHARED_PREFERENCES_TOKEN_KEY_DEFAULT_VALUE)
                        ?: SHARED_PREFERENCES_TOKEN_KEY_DEFAULT_VALUE))
        } else {
            viewModel.getAllTracksFromDataBase()
        }
    }

    private fun initRecyclerView() {
        trackAdapter = TrackAdapter()
        trackAdapter?.onTrackAdapterItemClickListener = this
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL))
            adapter = trackAdapter
        }
    }

    private fun setMainFragmentListeners() {
        with(binding) {
            navigationFab.setOnClickListener {
                navigateToFragment(JoggingFragment())
            }
            tvLogOut.setOnClickListener {
                setMenuExitButton(navigateToFragment, EntryFragment(), sharedPreferences!!)
            }
        }
        setPullToRefreshListener()
    }

    private fun setPullToRefreshListener() {
        binding.pullToRefreshLayout.setOnRefreshListener {
            viewModel.getAllTracksFromDataBase()
            viewModel.synchronizeWithServer()
            binding.pullToRefreshLayout.isRefreshing = false
        }
    }

    private val navigateToFragment: (Fragment) -> Unit = { fragment ->
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()

        if (arguments?.getBoolean(FIRST_APP_USE_EXTRA_KEY) != true) {
            viewModel.synchronizeWithServer()
        }
        networkTrackListObserver = Observer { recyclerViewTrackList ->
            if (arguments?.getBoolean(FIRST_APP_USE_EXTRA_KEY) == true) {
                viewModel.saveServerTracksToDataBase()
            }
            trackAdapter?.submitList(recyclerViewTrackList
                .sortedByDescending { it.startTime })
            binding.progressBar.isVisible = false
        }
        dataBaseTrackListObserver = Observer { recyclerViewTrackList ->
            trackAdapter?.submitList(recyclerViewTrackList
                .sortedByDescending { it.startTime })
            binding.progressBar.isVisible = false
        }

        viewModel.recyclerViewTrackList.observe(this, networkTrackListObserver!!)
        viewModel.dataBaseTrackList.observe(this, dataBaseTrackListObserver!!)
    }

    override fun onStop() {
        viewModel.recyclerViewTrackList.removeObserver(networkTrackListObserver!!)
        viewModel.dataBaseTrackList.removeObserver(dataBaseTrackListObserver!!)

        super.onStop()
    }

    override fun onItemClick(
        startLongitude: Double,
        startLatitude: Double,
        finishLongitude: Double,
        finishLatitude: Double,
        joggingTime: String,
        distance: String
    ) {
        val bundle = Bundle()
        bundle.putDoubleArray(
            LOCATION_COORDINATES_EXTRA_KEY, doubleArrayOf(
                startLongitude, startLatitude,
                finishLongitude, finishLatitude
            )
        )
        bundle.putString(TRACK_JOGGING_TIME_EXTRA_KEY, joggingTime)
        bundle.putString(TRACK_DISTANCE_EXTRA_KEY, distance)
        val fragment = TrackFragment()
        fragment.arguments = bundle

        navigateToFragment(fragment)
    }
}