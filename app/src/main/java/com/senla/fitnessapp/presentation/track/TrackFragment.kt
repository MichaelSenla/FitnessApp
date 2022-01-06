package com.senla.fitnessapp.presentation.track

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.senla.fitnessapp.R
import com.senla.fitnessapp.databinding.FragmentTrackBinding
import com.senla.fitnessapp.presentation.entry.EntryFragment
import com.senla.fitnessapp.presentation.main.MainFragment
import com.senla.fitnessapp.presentation.navigation.SideNavigation
import com.senla.fitnessapp.presentation.notification.NotificationFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackFragment : Fragment(), AddMapsMarkers {

    companion object {
        var mapFragment: SupportMapFragment? = null
        var googleMap: GoogleMap? = null
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
        setNavigationMenuButtons()
        setMap()
    }

    private fun setMap() {
        mapFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment?
        mapFragment?.getMapAsync(OnMapReadyCallback {
            googleMap = it
        })
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

    override fun addStartMarker(latLng: LatLng) {
//        googleMap?.addMarker(MarkerOptions().position(latLng).title())
    }

    override fun addFinishMarker(latLng: LatLng) {
        TODO("Not yet implemented")
    }
}

interface AddMapsMarkers {
    fun addStartMarker(latLng: LatLng)
    fun addFinishMarker(latLng: LatLng)
}