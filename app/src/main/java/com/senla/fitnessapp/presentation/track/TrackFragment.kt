package com.senla.fitnessapp.presentation.track

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.senla.fitnessapp.R
import com.senla.fitnessapp.databinding.FragmentTrackBinding
import com.senla.fitnessapp.presentation.entry.EntryFragment
import com.senla.fitnessapp.presentation.main.MainFragment
import com.senla.fitnessapp.presentation.main.MainFragment.Companion.LOCATION_COORDINATES_EXTRA_KEY
import com.senla.fitnessapp.presentation.main.MainFragment.Companion.TRACK_DISTANCE_EXTRA_KEY
import com.senla.fitnessapp.presentation.main.MainFragment.Companion.TRACK_JOGGING_TIME_EXTRA_KEY
import com.senla.fitnessapp.presentation.navigation.SideNavigation
import com.senla.fitnessapp.presentation.notification.NotificationFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackFragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val START_MARKER_LABEL = "Старт"
        private const val FINISH_MARKER_LABEL = "Финиш"
        private const val DISTANCE_TEXT = "Дистанция: \n"
        private const val METERS_TEXT = " метров"
        var mapFragment: SupportMapFragment? = null
        var map: GoogleMap? = null
    }

    private var _binding: FragmentTrackBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrackViewModel by viewModels()

    @set:Inject
    var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            tvJoggingTime.text = arguments?.getString(TRACK_JOGGING_TIME_EXTRA_KEY)
            tvJoggingDistance.text = StringBuilder(DISTANCE_TEXT).also {
                    it.append(arguments?.getString(TRACK_DISTANCE_EXTRA_KEY))
                    it.append(METERS_TEXT)
                }
        }
        setNavigationMenuButtons()
        setMap()
    }

    private fun setMap() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun setNavigationMenuButtons() {
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuItemNotification -> navigateToFragment(NotificationFragment())
                R.id.menuItemMain -> navigateToFragment(MainFragment())
                R.id.menuItemExit -> SideNavigation.setMenuExitButton(
                    navigateToFragment, EntryFragment(),
                    sharedPreferences!!
                )
            }
            true
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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val locationCoordinates = arguments?.getDoubleArray(LOCATION_COORDINATES_EXTRA_KEY)
        val startCoordinates = LatLng(
            locationCoordinates!!.component1(),
            locationCoordinates.component2()
        )

        map!!.addMarker(MarkerOptions().position(startCoordinates).title(START_MARKER_LABEL))
        map!!.addMarker(
            MarkerOptions().position(
                LatLng(
                    locationCoordinates.component3(),
                    locationCoordinates.component4()
                )
            ).title(FINISH_MARKER_LABEL)
        )
        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(startCoordinates, 20F))
    }
}