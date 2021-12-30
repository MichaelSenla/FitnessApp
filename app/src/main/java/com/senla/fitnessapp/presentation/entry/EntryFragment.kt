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
import androidx.lifecycle.Observer
import com.senla.fitnessapp.R
import com.senla.fitnessapp.common.Constants.SHARED_PREFERENCES_TOKEN_KEY
import com.senla.fitnessapp.data.network.models.LogInRequest
import com.senla.fitnessapp.data.network.models.LogInResponse
import com.senla.fitnessapp.data.network.models.RegisterRequest
import com.senla.fitnessapp.data.network.models.RegisterResponse
import com.senla.fitnessapp.databinding.FragmentEntryBinding
import com.senla.fitnessapp.presentation.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
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
    private lateinit var logInResponseObserver: Observer<LogInResponse>
    private lateinit var registrationResponseObserver: Observer<RegisterResponse>
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
        setObservers()
    }

    private fun setObservers() {
        setLogInObserver()
        setRegistrationObserver()
    }

    private fun setLogInObserver() {
        logInResponseObserver = Observer {
            if (it?.status == SERVER_SUCCESS_RESPONSE) {
                Log.e("Testing", it.status)
                sharedPreferences.edit().putString(SHARED_PREFERENCES_TOKEN_KEY,
                    it.token).apply()

                navigateToFragment(MainFragment())
            } else if (it?.status == SERVER_ERROR_RESPONSE){
                Toast.makeText(requireContext(), LOG_IN_ERROR_TEXT, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRegistrationObserver() {
        registrationResponseObserver = Observer {
            if (it.status == SERVER_SUCCESS_RESPONSE) {
                sharedPreferences.edit().putString(SHARED_PREFERENCES_TOKEN_KEY,
                    it.token).apply()

                navigateToFragment(MainFragment())
            } else if (it.status == SERVER_ERROR_RESPONSE) {
                Toast.makeText(requireContext(), REGISTER_ERROR_TEXT, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment).commit()
    }

    private fun setAllListeners() {
        binding.apply {
            btnSigningUp.setOnClickListener {
                entryViewModel.registerUser(REGISTER_QUERY_TEXT,
                    RegisterRequest(etEmail.text.toString(), etName.text.toString(),
                        etLastname.text.toString(), etPassword.text.toString()))
            }
            btnLogIn.setOnClickListener {
                entryViewModel.userLogIn(LOG_IN_QUERY_TEXT,
                    LogInRequest(etEmail.text.toString(), etPassword.text.toString()))
            }
            tvAuthenticationState.setOnClickListener {
                configureLayout()
            }
        }
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

    override fun onStart() {
        super.onStart()

        entryViewModel.logInResponse.observe(this, logInResponseObserver)
        entryViewModel.registerResponse.observe(this, registrationResponseObserver)
    }

    override fun onStop() {
        entryViewModel.logInResponse.removeObserver(logInResponseObserver)
        entryViewModel.registerResponse.removeObserver(registrationResponseObserver)

        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}