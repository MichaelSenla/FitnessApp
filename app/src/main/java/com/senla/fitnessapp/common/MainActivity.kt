package com.senla.fitnessapp.common

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.senla.fitnessapp.R
import com.senla.fitnessapp.presentation.jogging.JoggingFragment
import com.senla.fitnessapp.presentation.notification.NotificationFragment.Companion.JOGGING_FRAGMENT_EXTRA_KEY
import com.senla.fitnessapp.presentation.splash.SplashFragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Completable.error
import io.reactivex.rxjava3.core.Flowable.error
import io.reactivex.rxjava3.core.Maybe.error
import io.reactivex.rxjava3.core.Observable.error
import io.reactivex.rxjava3.core.Single.error
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.internal.disposables.EmptyDisposable.error
import io.reactivex.rxjava3.internal.subscriptions.EmptySubscription.error
import io.reactivex.rxjava3.internal.util.NotificationLite.error
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import retrofit2.Response.error
import retrofit2.adapter.rxjava3.Result.error

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    companion object {
        private const val RX_JAVA_ERROR_LOG_TAG = "RxJavaError"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar()
        navigateToFragment()
        handleRxJavaErrors()
    }

    private fun navigateToFragment() {
        if (intent.getBooleanExtra(JOGGING_FRAGMENT_EXTRA_KEY, false)) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, JoggingFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SplashFragment()).commit()
        }
    }

    private fun setSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun handleRxJavaErrors() {
        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) {
                Log.e(RX_JAVA_ERROR_LOG_TAG, e.message!!)
            } else {
                Thread.currentThread().also { thread ->
                    thread.uncaughtExceptionHandler?.uncaughtException(thread, e)
                }
            }
        }
    }
}