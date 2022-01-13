package com.senla.fitnessapp.presentation.navigation

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.senla.fitnessapp.common.Constants

class SideNavigation {

    companion object {

        fun setNavigationMenuButtons(
            binding: NavigationView, navigation: (Fragment) -> Unit,
            menuResource: Int, fragment: Fragment
        ) {
            binding.setNavigationItemSelectedListener {
                when (it.itemId) {
                    menuResource -> navigation(fragment)
                }
                true
            }
        }

        fun setMenuExitButton(
            navigation: (Fragment) -> Unit, fragment: Fragment,
            sharedPreferences: SharedPreferences
        ) {
            navigation(fragment)

            val editor = sharedPreferences.edit()
            editor.remove(Constants.SHARED_PREFERENCES_TOKEN_KEY)
            editor.apply()
        }
    }
}