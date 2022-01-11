package com.senla.fitnessapp.common

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.senla.fitnessapp.R
import com.senla.fitnessapp.presentation.jogging.JoggingFragment
import com.senla.fitnessapp.presentation.notification.broadcast.NotificationReceiver.Companion.JOGGING_FRAGMENT_EXTRA_KEY
import com.senla.fitnessapp.presentation.splash.SplashFragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins

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