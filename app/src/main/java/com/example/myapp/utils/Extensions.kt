package com.example.myapp.utils

import android.content.Context
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDelegate

object ThemeUtils {
    private const val PREF_THEME_MODE = "pref_theme_mode"
    const val THEME_LIGHT = "theme_light"
    const val THEME_DARK = "theme_dark"

    fun getCurrentThemeMode(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(PREF_THEME_MODE, THEME_LIGHT) ?: THEME_LIGHT
    }

    fun setCurrentThemeMode(context: Context, themeMode: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(PREF_THEME_MODE, themeMode)
        editor.apply()
    }

    fun applyTheme(context: Context) {
        val currentThemeMode = getCurrentThemeMode(context)
        if (currentThemeMode == THEME_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
fun AppCompatActivity.replaceFragment(
    container: Int,
    fragment: Fragment,
    tag: String = fragment.javaClass.simpleName
) = supportFragmentManager
    .beginTransaction()
    .replace(container, fragment, tag)
    .commit()