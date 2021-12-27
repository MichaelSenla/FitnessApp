package com.senla.fitnessapp.presentation.entry

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.senla.fitnessapp.R
import com.senla.fitnessapp.data.network.models.RegisterRequest
import com.senla.fitnessapp.data.network.models.RegisterResponse
import com.senla.fitnessapp.databinding.FragmentEntryBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EntryFragment : Fragment(R.layout.fragment_entry) {

    companion object {
        private const val SPANNABLE_LOG_IN_TEXT = "Войти"
        private const val SPANNABLE_SIGNING_UP_TEXT = "Зарегистрироваться"
        private const val LOG_IN_SPANNABLE_START_INDEX = 0
        private const val LOG_IN_SPANNABLE_END_INDEX = 5
        private const val SIGNING_UP_SPANNABLE_START_INDEX = 0
        private const val SIGNING_UP_SPANNABLE_END_INDEX = 18
        private const val SHARED_PREFERENCES_TOKEN_KEY = "SHARED_PREFERENCES_TOKEN_KEY"
        private const val REGISTER_TEXT = "register"
        private const val REGISTER_ERROR_TEXT = "Не удалось зарегестрироваться, попробуйте еще раз."
    }

    private var _binding: FragmentEntryBinding? = null
    private val binding get() = _binding!!
    private val entryViewModel: EntryViewModel by viewModels()
    private var logInFlag: Boolean = false

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var userRegisterResponse: LiveData<RegisterResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureLayout()
        setAllButtonsListeners()

    }

    private fun userLogIn(query: String, email: String, password: String) {
        entryViewModel.userLogIn(query, email, password)
    }

    private fun setListenerForRegistrationButton() {
        binding.apply {
            btSigningUp.setOnClickListener {
                Log.e("checking", "${userRegisterResponse?.value}")
                userRegisterResponse?.observe(this@EntryFragment, Observer {
                    if (it.status == "ok") {
                        sharedPreferences.edit()
                            .putString(SHARED_PREFERENCES_TOKEN_KEY,
                                userRegisterResponse?.value?.token).apply()
                    } else if (it.status == "error") {
                        Toast.makeText(requireContext(), REGISTER_ERROR_TEXT,
                            Toast.LENGTH_SHORT).show()
                    }
                })
                userRegisterResponse = entryViewModel.registerUser(REGISTER_TEXT, RegisterRequest(
                        etEmail.text.toString(),
                        etName.text.toString(),
                        etLastname.text.toString(),
                        etPassword.text.toString()))
            }
        }
    }

    private fun setListenerForEntryButton() {
        binding.apply {

        }
    }

    private fun setAllButtonsListeners() {
        setListenerForRegistrationButton()
        setListenerForEntryButton()
    }


    private fun configureLayout() {
        binding.apply {
            if (logInFlag) {
                tvAuthenticationState.text = entryViewModel
                    .createUnderlineSpannable(
                        text = SPANNABLE_SIGNING_UP_TEXT,
                        startIndex = SIGNING_UP_SPANNABLE_START_INDEX,
                        endIndex = SIGNING_UP_SPANNABLE_END_INDEX
                    )
                etLastname.isVisible = false
                etName.isVisible = false
                etRepeatPassword.isVisible = false
                logInFlag = false
            } else {
                tvAuthenticationState.text = entryViewModel
                    .createUnderlineSpannable(
                        text = SPANNABLE_LOG_IN_TEXT,
                        startIndex = LOG_IN_SPANNABLE_START_INDEX,
                        endIndex = LOG_IN_SPANNABLE_END_INDEX
                    )
                etLastname.isVisible = true
                etName.isVisible = true
                etRepeatPassword.isVisible = true
                logInFlag = true
            }
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}