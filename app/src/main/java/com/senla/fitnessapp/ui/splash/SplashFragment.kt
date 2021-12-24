package com.senla.fitnessapp.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.senla.fitnessapp.R
import com.senla.fitnessapp.databinding.SplashFragmentBinding
import com.senla.fitnessapp.ui.entry.EntryFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: SplashFragmentBinding? = null
    private val binding get() = _binding!!
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SplashFragmentBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
     navigateToEntryFragment()
     splashViewModel.rotateIcon(binding.imageIcon)
    }

    private fun navigateToEntryFragment(){
        Handler(Looper.getMainLooper()).postDelayed({
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, EntryFragment()).commit()
        }, 3000)
    }
}