package com.senla.fitnessapp.ui.splash

import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.view.View
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

class SplashViewModel() : ViewModel() {

    companion object {
        private const val BEGIN_ANIMATION_VALUE = 0f
        private const val END_ANIMATION_VALUE = 360f
        private const val ANIMATION_DURATION: Long = 3000
    }

    fun rotateIcon(view: View) {
        ObjectAnimator.ofFloat(view, View.ROTATION_Y, BEGIN_ANIMATION_VALUE, END_ANIMATION_VALUE)
            .apply {
                duration = ANIMATION_DURATION
                repeatCount = INFINITE
                start()
            }
    }
}