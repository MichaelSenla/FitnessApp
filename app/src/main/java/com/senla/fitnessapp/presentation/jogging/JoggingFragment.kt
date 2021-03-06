package com.senla.fitnessapp.presentation.jogging

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
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
import com.senla.fitnessapp.common.Functions.isNetworkAvailable
import com.senla.fitnessapp.data.database.models.DataBaseSavedTrack
import com.senla.fitnessapp.data.database.models.DataBaseTrack
import com.senla.fitnessapp.data.network.models.saveTrack.saveTrackRequest.Point
import com.senla.fitnessapp.data.network.models.saveTrack.saveTrackRequest.SaveTrackRequest
import com.senla.fitnessapp.databinding.FragmentJoggingBinding
import com.senla.fitnessapp.databinding.LayoutPopupWindowBinding
import com.senla.fitnessapp.presentation.jogging.location.GpsLocation
import com.senla.fitnessapp.presentation.jogging.location.LocationListener
import com.senla.fitnessapp.presentation.jogging.service.TimerService
import com.senla.fitnessapp.presentation.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JoggingFragment : Fragment(), GpsLocation {

    companion object {
        private const val DELAY = 600L
        private const val SAVE_TRACK_QUERY = "save"
        private const val MILLISECONDS_DELAY_OF_EMITTING_NUMBER = 100
        private const val ON_BACK_PRESSED_ERROR_TOAST = "?????? ???????????? ?????????????? ???? ???????????? \"??????????\"," +
                " ????????????????????."
        private const val FINISHED_DISTANCE_TEXT = "???????????????????? ??????????????????"
        private const val LOCATION_IS_EMPTY_ERROR = "????????????????, ???? ???????????????????? ???????????????? ????????????" +
                " ?? ????????????????????????????."
        private const val GPS_UPDATE_MIN_TIME = 1000L
        private const val GPS_UPDATE_MIN_DISTANCE = 1F
        private const val GPS_PERMISSION_ERROR = "GPS permissions weren't provided"
        private const val DEFAULT_START_LONGITUDE_VALUE = 37.618423
        private const val DEFAULT_START_LATITUDE_VALUE = 55.751244
        private const val SHARED_PREFERENCES_TOKEN_KEY_DEFAULT_VALUE = ""
        private const val POP_UP_WINDOW_X_AXIS_VALUE = 0
        private const val POP_UP_WINDOW_Y_AXIS_VALUE = 0
        private const val TIME_DEFAULT_VALUE = 0.0

        var lastLocation: Location? = null
    }

    private var _binding: FragmentJoggingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: JoggingViewModel by viewModels()
    private var timerStarted = false
    private var distance = 0
    private var time = 0.0
    private var startLongitude: Double = 37.618423
    private var startLatitude: Double = 55.751244
    private var point: Point? = null

    @set:Inject
    var sharedPreferences: SharedPreferences? = null
    private var serviceIntent: Intent? = null
    private var flipAnimator: AnimatorSet? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var isFinished: Boolean? = null
    private var tracksStartTime: Long? = null
    private var popupWindow: PopupWindow? = null
    private var popupWindowView: View? = null
    private var popupWindowLayout: LayoutPopupWindowBinding? = null
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
                            requireContext(), GPS_PERMISSION_ERROR,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    if (actionMap.value) {
                        checkPermissions()
                    } else {
                        Toast.makeText(
                            requireContext(), GPS_PERMISSION_ERROR,
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

    @SuppressLint("MissingPermission")
    private fun getStartLocation() {
        val location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        startLongitude = location?.longitude ?: DEFAULT_START_LONGITUDE_VALUE
        startLatitude = location?.latitude ?: DEFAULT_START_LATITUDE_VALUE
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
                    GPS_UPDATE_MIN_TIME,
                    GPS_UPDATE_MIN_DISTANCE, locationListener!!
                )
            }
        }
    }

    private fun setButtonStartListener(getGps: () -> Unit) {
        binding.btnStart.setOnClickListener {
            isFinished = false
            tracksStartTime = System.currentTimeMillis()
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
            getStartLocation()
        }
    }

    private fun setButtonFinishListener() {
        binding.btnFinish.setOnClickListener {
            stopTimer()
            isFinished = true
            buttonFinishNetworkAvailable()
            buttonFinishNoNetworkConnection()
        }
    }

    private fun buttonFinishNetworkAvailable() {
        @RequiresApi(Build.VERSION_CODES.M)
        if (isNetworkAvailable(requireContext())) {
            with(binding) {
                Handler(Looper.getMainLooper()).postDelayed({
                    tvTime.isVisible = true
                    tvDistance.isVisible = true
                    btnFinish.isVisible = false
                    tvDistance.text = StringBuilder(FINISHED_DISTANCE_TEXT)
                        .append(": \n$distance ????????????")
                }, DELAY)
                with(flipAnimator) {
                    this?.setTarget(btnFinish)
                    this?.start()
                }
            }
            with(viewModel) {
                insertTrack(
                    DataBaseTrack(
                        startTime = tracksStartTime!!,
                        distance = distance.toString(),
                        joggingTime = (time * MILLISECONDS_DELAY_OF_EMITTING_NUMBER).toLong(),
                        startLongitude = startLongitude, startLatitude = startLatitude,
                        finishLongitude = point?.lng ?: startLongitude,
                        finishLatitude = point?.lat ?: startLatitude
                    )
                )
                saveTrack(
                    SAVE_TRACK_QUERY, SaveTrackRequest(
                        sharedPreferences
                            ?.getString(
                                SHARED_PREFERENCES_TOKEN_KEY,
                                SHARED_PREFERENCES_TOKEN_KEY_DEFAULT_VALUE)
                            ?: SHARED_PREFERENCES_TOKEN_KEY_DEFAULT_VALUE,
                        beginsAt = tracksStartTime!!,
                        time = (time * MILLISECONDS_DELAY_OF_EMITTING_NUMBER).toLong(),
                        distance = distance, points = listOf(point ?: Point(0.0, 0.0))
                    )
                )
            }
        }
    }

    private fun buttonFinishNoNetworkConnection() {
        @RequiresApi(Build.VERSION_CODES.M)
        if (!isNetworkAvailable(requireContext())) {
            with(viewModel) {
                insertSavedTrack(
                    DataBaseSavedTrack(
                        startTime = tracksStartTime!!,
                        distance = distance.toString(),
                        joggingTime = (time * MILLISECONDS_DELAY_OF_EMITTING_NUMBER).toLong(),
                        startLongitude = startLongitude, startLatitude = startLatitude,
                        finishLongitude = point?.lng ?: startLongitude,
                        finishLatitude = point?.lat ?: startLatitude
                    )
                )
                insertTrack(
                    DataBaseTrack(
                        startTime = tracksStartTime!!,
                        distance = distance.toString(),
                        joggingTime = (time * MILLISECONDS_DELAY_OF_EMITTING_NUMBER).toLong(),
                        startLongitude = startLongitude, startLatitude = startLatitude,
                        finishLongitude = point?.lng ?: startLongitude,
                        finishLatitude = point?.lat ?: startLatitude
                    )
                )
            }

            popupWindow = PopupWindow(requireContext())
            popupWindowView = layoutInflater.inflate(
                R.layout.layout_popup_window, null, false
            )
            popupWindow?.contentView = popupWindowView
            popupWindow?.showAtLocation(popupWindowView, Gravity.CENTER,
                POP_UP_WINDOW_X_AXIS_VALUE, POP_UP_WINDOW_Y_AXIS_VALUE)
            popupWindowLayout = LayoutPopupWindowBinding.bind(popupWindowView!!)

            with(binding) {
                btnStart.isVisible = false
                tvTime.isVisible = false
                btnFinish.isVisible = false
                tvDistance.isVisible = false
            }
            setPopMenuListeners()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setPopMenuListeners() {
        popupWindowLayout?.tvNoInternetConnectionError?.setOnClickListener {
            popupWindow?.dismiss()
            if (isNetworkAvailable(requireContext())) {
                with(binding) {
                    btnStart.isVisible = true
                    tvTime.isVisible = true
                    btnFinish.isVisible = true
                    tvDistance.isVisible = true
                }
            } else {
                popupWindowLayout!!.tvNoInternetConnectionError.text =
                    getString(R.string.layout_popup_window_restart_app_text)
                popupWindow?.showAtLocation(view, Gravity.CENTER,
                    POP_UP_WINDOW_X_AXIS_VALUE, POP_UP_WINDOW_Y_AXIS_VALUE)
            }
        }
        popupWindowLayout?.btnGoToMainFragment?.setOnClickListener {
            popupWindow?.dismiss()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MainFragment()).commit()
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFinished == false) {
                    Toast.makeText(
                        requireContext(),
                        ON_BACK_PRESSED_ERROR_TOAST,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, MainFragment()).commit()
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

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, TIME_DEFAULT_VALUE)
            binding.tvTime.text = viewModel.getTimeStringFromDouble(time)
        }
    }

    override fun onDestroyView() {
        _binding = null
        locationManager = null
        locationListener = null
        requireContext().unregisterReceiver(updateTime)

        super.onDestroyView()
    }

    override fun onLocationChanged(location: Location) {
        if (location.hasSpeed() && lastLocation != null) {
            distance += lastLocation?.distanceTo(location)?.toInt()!!
        }

        point = Point(location.longitude, location.latitude)

        lastLocation = location
    }

    override fun onStart() {
        onBackPressed()

        super.onStart()
    }
}