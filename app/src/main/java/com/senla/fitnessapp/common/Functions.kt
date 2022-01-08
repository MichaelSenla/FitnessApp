package com.senla.fitnessapp.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.senla.fitnessapp.R

object Functions {
    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkAvailable(context: Context) =
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }

    fun navigateToFragment(fragment: Fragment, navigateToFragment: Fragment) {
        fragment.requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, navigateToFragment).commit()
    }
}