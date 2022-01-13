package com.senla.fitnessapp.presentation.jogging.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.*

class TimerService : Service() {

    companion object {
        const val TIMER_UPDATED = "timedUpdated"
        const val TIME_EXTRA = "timeExtra"
        const val TIME_DEFAULT_VALUE = 0.0
        const val TIME_DELAY = 0L
        const val TIME_PERIOD = 100L
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private val timer = Timer()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val time = intent.getDoubleExtra(TIME_EXTRA, TIME_DEFAULT_VALUE)
        timer.scheduleAtFixedRate(TimeTask(time), TIME_DELAY, TIME_PERIOD)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer.cancel()

        super.onDestroy()
    }

    private inner class TimeTask(private var time: Double) : TimerTask() {
        override fun run() {
            val intent = Intent(TIMER_UPDATED)
            time++
            intent.putExtra(TIME_EXTRA, time)
            sendBroadcast(intent)
        }
    }
}