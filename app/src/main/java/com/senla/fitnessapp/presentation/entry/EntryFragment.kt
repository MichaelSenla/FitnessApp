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
import com.senla.fitnessapp.R
import com.senla.fitnessapp.common.Constants.SHARED_PREFERENCES_TOKEN_KEY
import com.senla.fitnessapp.data.network.models.LogInRequest
import com.senla.fitnessapp.data.network.models.RegisterRequest
import com.senla.fitnessapp.databinding.FragmentEntryBinding
import com.senla.fitnessapp.presentation.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EntryFragment: Fragment(R.layout.fragment_entry) {

    companion object {
        private const val SPANNABLE_LOG_IN_TEXT = "Войти"
        private const val SPANNABLE_SIGNING_UP_TEXT = "Зарегистрироваться"
        private const val LOG_IN_SPANNABLE_START_INDEX = 0
        private const val LOG_IN_SPANNABLE_END_INDEX = 5
        private const val SIGNING_UP_SPANNABLE_START_INDEX = 0
        private const val SIGNING_UP_SPANNABLE_END_INDEX = 18
        private const val REGISTER_QUERY_TEXT = "register"
        private const val LOG_IN_QUERY_TEXT = "login"
        private const val REGISTER_ERROR_TEXT = "Не удалось зарегестрироваться, пожалуйста, " +
                "попробуйте еще раз."
        private const val LOG_IN_ERROR_TEXT = "Не удалось войти, пожалуйста, " +
                "проверьте введённые данные."
        private const val SERVER_SUCCESS_RESPONSE = "ok"
        private const val SERVER_ERROR_RESPONSE = "error"
    }

    private var _binding: FragmentEntryBinding? = null
    private val binding get() = _binding!!
    private val entryViewModel: EntryViewModel by viewModels()
    private var logInFlag: Boolean = true

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (sharedPreferences.getString(SHARED_PREFERENCES_TOKEN_KEY, null) != null) {
            navigateToFragment(MainFragment())
        } else {
            configureLayout()
        }
        setAllListeners()
    }

    private fun setListenerForRegistrationButton() {
        binding.apply {
            btnSigningUp.setOnClickListener {
                val registerResponse = entryViewModel.registerUser(REGISTER_QUERY_TEXT,
                    RegisterRequest(etEmail.text.toString(), etName.text.toString(),
                        etLastname.text.toString(), etPassword.text.toString())).value
                Log.e("Testing", "${registerResponse?.status}")
                if (registerResponse?.status == SERVER_SUCCESS_RESPONSE) {
                    sharedPreferences.edit().putString(SHARED_PREFERENCES_TOKEN_KEY,
                        registerResponse.token).apply()

                    navigateToFragment(MainFragment())
                } else if (registerResponse?.status == SERVER_ERROR_RESPONSE) {
                    Toast.makeText(requireContext(), REGISTER_ERROR_TEXT, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setListenerForEntryButton() {
        binding.apply {
            btnLogIn.setOnClickListener {
                val logInResponse = entryViewModel.userLogIn(LOG_IN_QUERY_TEXT,
                    LogInRequest(etEmail.text.toString(), etPassword.text.toString())).value
                Log.e("Testing", "${logInResponse?.status}")
                if (logInResponse?.status == SERVER_SUCCESS_RESPONSE) {
                    sharedPreferences.edit().putString(SHARED_PREFERENCES_TOKEN_KEY,
                        logInResponse.token).apply()

                    navigateToFragment(MainFragment())
                } else {

                    Toast.makeText(requireContext(), LOG_IN_ERROR_TEXT, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment).commit()
    }

    private fun setAuthenticationStateListener() {
        binding.tvAuthenticationState.setOnClickListener {
            configureLayout()
        }
    }

    private fun setAllListeners() {
        setListenerForRegistrationButton()
        setListenerForEntryButton()
        setAuthenticationStateListener()
    }

    private fun configureLayout() {
        binding.apply {
            if (logInFlag) {
                tvAuthenticationState.text = entryViewModel
                    .createUnderlineSpannable(
                        text = SPANNABLE_LOG_IN_TEXT,
                        startIndex = LOG_IN_SPANNABLE_START_INDEX,
                        endIndex = LOG_IN_SPANNABLE_END_INDEX
                    )
                etLastname.isVisible = true
                etName.isVisible = true
                etRepeatPassword.isVisible = true
                btnSigningUp.isVisible = true
                btnLogIn.isVisible = false
                logInFlag = false
            } else {
                tvAuthenticationState.text = entryViewModel
                    .createUnderlineSpannable(
                        text = SPANNABLE_SIGNING_UP_TEXT,
                        startIndex = SIGNING_UP_SPANNABLE_START_INDEX,
                        endIndex = SIGNING_UP_SPANNABLE_END_INDEX
                    )
                etLastname.isVisible = false
                etName.isVisible = false
                etRepeatPassword.isVisible = false
                btnLogIn.isVisible = true
                btnSigningUp.isVisible = false
                logInFlag = true
            }
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}