package com.senla.fitnessapp.ui.entry

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.senla.fitnessapp.R
import com.senla.fitnessapp.data.network.models.RegisterResponse
import com.senla.fitnessapp.databinding.EntryFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EntryFragment : Fragment(R.layout.entry_fragment) {

    companion object {
        private const val SPANNABLE_LOG_IN_TEXT = "Войти"
        private const val SPANNABLE_SIGNING_UP_TEXT = "Зарегистрироваться"
        private const val LOG_IN_SPANNABLE_START_INDEX = 0
        private const val LOG_IN_SPANNABLE_END_INDEX = 5
        private const val SIGNING_UP_SPANNABLE_START_INDEX = 0
        private const val SIGNING_UP_SPANNABLE_END_INDEX = 18
        private const val SHARED_PREFERENCES_TOKEN_KEY = "SHARED_PREFERENCES_TOKEN_KEY"
    }

    private var _binding: EntryFragmentBinding? = null
    private val binding get() = _binding!!
    private val entryViewModel: EntryViewModel by viewModels()
    private var logInFlag: Boolean = false
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EntryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureLayout()
        setListenersForEntryButtons()
    }

    private fun registerUser(email: String, firstName: String,
                             lastName: String, password: String): LiveData<RegisterResponse> =
        entryViewModel.registerUser(email, firstName, lastName, password)


    private fun userLogIn(email: String, password: String) {
        entryViewModel.userLogIn(email, password)
    }

    private fun setListenersForEntryButtons() {
        binding.apply {
            btSigningUp.setOnClickListener {
                val registerResponse = registerUser(etEmail.text.toString(),
                    etName.text.toString(), etLastname.text.toString(), etPassword.text.toString())
                sharedPreferences.edit()
                    .putString(SHARED_PREFERENCES_TOKEN_KEY, registerResponse.value?.token).apply()
                registerResponse.value?.let { it1 -> Log.e("CHECKING", it1.token) }
            }
        }
    }

    private fun configureLayout() {
        binding.apply {
            if (logInFlag) {
                tvLogIn.text = entryViewModel
                    .createUnderlineSpannable(text = SPANNABLE_SIGNING_UP_TEXT,
                        startIndex = SIGNING_UP_SPANNABLE_START_INDEX,
                        endIndex = SIGNING_UP_SPANNABLE_END_INDEX)
                etLastname.isVisible = false
                etName.isVisible = false
                etRepeatPassword.isVisible = false
                logInFlag = false
            } else {
                tvSigningUp.text = entryViewModel
                    .createUnderlineSpannable(text = SPANNABLE_LOG_IN_TEXT,
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
}