package com.senla.fitnessapp.presentation.distance

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.senla.fitnessapp.R
import com.senla.fitnessapp.databinding.FragmentDistanceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DistanceFragment : Fragment() {

    private lateinit var viewModel: DistanceViewModel
    private var _binding: FragmentDistanceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDistanceBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}