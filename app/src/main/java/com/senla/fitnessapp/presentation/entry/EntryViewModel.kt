package com.senla.fitnessapp.presentation.entry

import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.senla.fitnessapp.data.Repository
import com.senla.fitnessapp.data.network.models.LogInResponse
import com.senla.fitnessapp.data.network.models.RegisterRequest
import com.senla.fitnessapp.data.network.models.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val compositeDisposable: CompositeDisposable,
    private val repository: Repository
) : ViewModel() {

    fun createUnderlineSpannable(text: String, startIndex: Int, endIndex: Int): SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            UnderlineSpan(), startIndex,
            endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        return spannableString
    }

    fun registerUser(query: String, registerRequest: RegisterRequest): LiveData<RegisterResponse> =
        repository.registerUser(query, registerRequest)

    fun userLogIn(query: String, email: String, password: String): LiveData<LogInResponse> =
        repository.userLogIn(query, email, password)

    override fun onCleared() {
        compositeDisposable.dispose()

        super.onCleared()
    }
}