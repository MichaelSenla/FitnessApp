package com.senla.fitnessapp.presentation.jogging

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.senla.fitnessapp.R
import com.senla.fitnessapp.common.Constants.SHARED_PREFERENCES_TOKEN_KEY
import com.senla.fitnessapp.data.database.models.Track
import com.senla.fitnessapp.data.network.models.saveTrackRequest.Point
import com.senla.fitnessapp.data.network.models.saveTrackRequest.SaveTrackRequest
import com.senla.fitnessapp.databinding.FragmentJoggingBinding
import com.senla.fitnessapp.databinding.LayoutPopupWindowBinding
import com.senla.fitnessapp.presentation.jogging.service.TimerService
import com.senla.fitnessapp.presentation.location.GpsLocation
import com.senla.fitnessapp.presentation.location.LocationListener
import com.senla.fitnessapp.presentation.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JoggingFragment : Fragment(), GpsLocation {

    companion object {
        private const val DELAY = 600L
        private const val SAVE_TRACK_QUERY = "save"
        private const val START_TIME_COUNT_NUMBER = 10
        private const val ON_BACK_PRESSED_ERROR_TOAST = "Для начала нажмите на кнопку \"Финиш\"," +
                " пожалуйста."
        private const val FINISHED_DISTANCE_TEXT = "Пройденная дистанция"
    }

    private var _binding: FragmentJoggingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: JoggingViewModel by viewModels()
    private var timerStarted = false
    private var distance = 0
    private var time = 0.0
    private var point = Point(0.0,0.0)
    @set:Inject
    var sharedPreferences: SharedPreferences? = null
    private var serviceIntent: Intent? = null
    private var flipAnimator: AnimatorSet? = null
    private var locationManager: LocationManager? = null
    private var lastLocation: Location? = null
    private var locationListener: LocationListener? = null
    private var isFinished = false
    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { actionMap ->
            when (actionMap.key) {
                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    if (actionMap.value) {
                        checkPermissions()
                    } else {
                        Toast.makeText(
                            requireContext(), "GPS permissions weren't provided",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    if (actionMap.value) {
                        checkPermissions()
                    } else {
                        Toast.makeText(
                            requireContext(), "GPS permissions weren't provided",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoggingBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initLocation()
        checkPermissions()
        setFlipAnimation()
        setButtonFinishListener()

        requireContext().registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        serviceIntent = Intent(requireContext(), TimerService::class.java)
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        } else {
            setButtonStartListener {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1,
                    1F, locationListener!!
                )
            }
        }
    }

    private fun setButtonStartListener(getGps: () -> Unit) {
        binding.btnStart.setOnClickListener {
            with(binding) {
                with(flipAnimator) {
                    this?.setTarget(btnStart)
                    this?.start()
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    btnStart.isVisible = false
                    btnFinish.isVisible = true
                    tvTime.isVisible = true
                    startTimer()
                }, DELAY)
            }
            getGps()
        }
    }


    private fun setButtonFinishListener() {
        binding.btnFinish.setOnClickListener {
            stopTimer()
            isFinished  = true
            with(binding) {
                tvTime.isVisible = true
                tvDistance.isVisible = true
                btnFinish.isVisible = false
                tvDistance.text = StringBuilder(FINISHED_DISTANCE_TEXT)
                    .append(": \n$distance метров")
            }
            viewModel.insertTrack(Track(destination = distance.toString()))

            @RequiresApi(Build.VERSION_CODES.M)
            if (!isNetworkAvailable(requireContext())) {
                val popupWindow = PopupWindow(requireContext())
                val view = layoutInflater.inflate(R.layout.layout_popup_window,
                    null)
                popupWindow.contentView = view
                popupWindow.showAtLocation(view, Gravity.CENTER,0,0)
                val popupWindowLayout = LayoutPopupWindowBinding.bind(view)
                with(binding) {
                    btnStart.isVisible = false
                    tvTime.isVisible = false
                    btnFinish.isVisible = false
                    tvDistance.isVisible = false
                    tvSpeed.isVisible = false
                }
                popupWindowLayout.tvNoInternetConnectionError.setOnClickListener {
                    popupWindow.dismiss()
                    if (isNetworkAvailable(requireContext())) {
                        with(binding) {
                            btnStart.isVisible = true
                            tvTime.isVisible = true
                            btnFinish.isVisible = true
                            tvDistance.isVisible = true
                            tvSpeed.isVisible = true
                        }
                    } else {
                        popupWindowLayout.tvNoInternetConnectionError.text =
                            getString(R.string.layout_popup_window_restart_app_text)
                        popupWindow.showAtLocation(view, Gravity.CENTER,0,0)
                    }
                }
                popupWindowLayout.btnGoToMainFragment.setOnClickListener {
                    popupWindow.dismiss()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, MainFragment()).commit()
                }
            }
            viewModel.saveTrack(
                SAVE_TRACK_QUERY, SaveTrackRequest(sharedPreferences
                        ?.getString(SHARED_PREFERENCES_TOKEN_KEY, "") ?: "",
                    beginsAt = System.currentTimeMillis(),
                    time = (time / START_TIME_COUNT_NUMBER).toInt(),
                    distance = distance,
                    points = listOf(point),
                    id = null
                )
            )
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isFinished) {
                    Toast.makeText(requireContext(),
                        ON_BACK_PRESSED_ERROR_TOAST,
                        Toast.LENGTH_SHORT).show()
                } else {
                    requireActivity().onBackPressed()
                }
            }
        })
    }

    private fun setFlipAnimation() {
        val scale = requireContext().resources.displayMetrics.density

        binding.btnStart.cameraDistance = 8000 * scale

        flipAnimator = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.front_animator
        ) as AnimatorSet
    }

    private fun startTimer() {
        serviceIntent?.putExtra(TimerService.TIME_EXTRA, time)
        requireContext().startService(serviceIntent)
        timerStarted = true
    }

    private fun stopTimer() {
        requireContext().stopService(serviceIntent)
        timerStarted = false
    }

    private fun initLocation() {
        locationManager = requireContext()
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener()
        locationListener?.gpsLocation = this
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkAvailable(context: Context) =
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.tvTime.text = viewModel.getTimeStringFromDouble(time)
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    override fun onLocationChanged(location: Location) {
        if (location.hasSpeed() && lastLocation != null) {
            distance += lastLocation?.distanceTo(location)?.toInt()!!
        }

        point = Point(location.longitude, location.latitude)

        location.accuracy = 5F
        lastLocation = location

        binding.tvDistance.text = distance.toString()
        binding.tvSpeed.text = location.speed.toString()
    }

    override fun onStart() {
        onBackPressed()

        super.onStart()
    }
}