package com.senla.fitnessapp.presentation.jogging

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.senla.fitnessapp.R
import com.senla.fitnessapp.databinding.FragmentJoggingBinding
import com.senla.fitnessapp.presentation.jogging.service.TimerService
import com.senla.fitnessapp.presentation.location.Location
import com.senla.fitnessapp.presentation.location.LocationListener

class JoggingFragment : Fragment(), Location {

    companion object {
        private const val DELAY = 600L
    }

    private var _binding: FragmentJoggingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: JoggingViewModel by viewModels()
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    private lateinit var flipAnimator: AnimatorSet
    private lateinit var locationManager: LocationManager
    private lateinit var location: Location
    private lateinit var locationListener: LocationListener
    private val requestPermissions = registerForActivityResult(ActivityResultContracts
        .RequestMultiplePermissions()) { permissions ->
        permissions.forEach { actionMap ->
            when (actionMap.key) {
                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    if (actionMap.value) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, minDistancem = 1, )
                        Log.i("DEBUG", "Coarse location permission is granted")
                    } else {
                        !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                    }
                }
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    if (actionMap.value) {
                        Log.i("DEBUG", "Fine location permission is granted")
                    } else {
                        !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
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

                    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                        setFlipAnimation()
                        setAllListeners()

                        requireContext().registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

                        serviceIntent = Intent(requireContext(), TimerService::class.java)
                        requestPermissions.launch(
                            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION))
                    }

                    private fun setAllListeners() {
                        setButtonStartListener()
                        setButtonFinishListener()
                    }

                    private fun setButtonStartListener() {
                        binding.btnStart.setOnClickListener {
                            with(binding) {
                                with(flipAnimator) {
                                    setTarget(btnStart)
                                    start()
                                }
                                Handler(Looper.getMainLooper()).postDelayed({
                                    btnStart.isVisible = false
                                    btnFinish.isVisible = true
                                    tvTime.isVisible = true
                                    startTimer()
                                }, DELAY)
                            }
                        }
                    }

                    private fun setButtonFinishListener() {
                        binding.btnFinish.setOnClickListener {
                            stopTimer()
                            with(binding) {
                                tvTime.isVisible = true
                                btnFinish.isVisible = false
                            }
                        }
                    }

                    private fun setFlipAnimation() {
                        val scale = requireContext().resources.displayMetrics.density

                        binding.btnStart.cameraDistance = 8000 * scale

                        flipAnimator = AnimatorInflater.loadAnimator(requireContext(),
                            R.animator.front_animator) as AnimatorSet
                    }

                    private fun startTimer() {
                        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
                        requireContext().startService(serviceIntent)
                        timerStarted = true
                    }

                    private fun stopTimer() {
                        requireContext().stopService(serviceIntent)
                        timerStarted = false
                    }

                    private fun initLocation() {
                        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE)
                                as LocationManager
                        locationListener = LocationListener()
                        locationListener.onLocationChanged()

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

                    }
                }