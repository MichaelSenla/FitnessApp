package com.senla.fitnessapp.presentation.navigation

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.senla.fitnessapp.R
import com.senla.fitnessapp.common.Constants
import com.senla.fitnessapp.presentation.entry.EntryFragment

class SideNavigation {

    companion object {

        fun setNavigationMenuButtons(
            binding: NavigationView, navigation: (Fragment) -> Unit,
            menuResource: Int, fragment: Fragment, sharedPreferences: SharedPreferences) {
            binding.setNavigationItemSelectedListener {
                when (it.itemId) {
                    menuResource -> navigation(fragment)
                    R.id.menuItemExit -> setMenuExitButton(navigation, EntryFragment(),
                        sharedPreferences)
                }
                true
            }
        }

        fun setMenuExitButton(
            navigation: (Fragment) -> Unit, fragment: Fragment,
            sharedPreferences: SharedPreferences) {
            navigation(fragment)

            val editor = sharedPreferences.edit()
            editor.remove(Constants.SHARED_PREFERENCES_TOKEN_KEY)
            editor.apply()
        }
    }
}