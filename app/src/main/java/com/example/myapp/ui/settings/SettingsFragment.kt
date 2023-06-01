package com.example.myapp.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myapp.MainActivity
import com.example.myapp.R
import com.example.myapp.databinding.FragmentSettingsBinding
import com.example.myapp.ui.login.SignInFragment
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SettingsFragment: Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as MainActivity
        activity.showMenu(false)



        binding = FragmentSettingsBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)


        binding.apply {
            languageButton.setOnClickListener {
                // Открываем диалоговое окно с выбором языка
                showLanguageSelectionDialog()
            }

            themeSwitch.isChecked = isNightModeEnabled()

            themeSwitch.setOnCheckedChangeListener { _, isChecked ->
                setNightModeEnabled(isChecked)
            }

            logoutButton.setOnClickListener {
                showLogoutConfirmationDialog()
            }
        }
    }

    private fun showLanguageSelectionDialog() {
        val languageOptions = arrayOf("English", "Українська") // Варианты выбора языка
        val currentLanguageIndex = if (isLanguageUkrainian()) 1 else 0 // Индекс текущего выбранного языка

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.hange_language)
            .setSingleChoiceItems(languageOptions, currentLanguageIndex) { dialog, which ->
                val selectedLanguage = when (which) {
                    0 -> "en" // Английский язык
                    1 -> "uk" // Украинский язык
                    else -> "en" // По умолчанию выбран английский язык
                }
                changeLanguage(selectedLanguage)
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        alertDialog.show()
    }

    private fun isLanguageUkrainian(): Boolean {
        val currentLocale = resources.configuration.locale
        return currentLocale.language == "uk"
    }

    private fun changeLanguage(languageCode: String) {
        val configuration = resources.configuration
        val newLocale = Locale(languageCode)
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        saveSelectedLanguage(languageCode)
        requireActivity().recreate() // Пересоздаем активити, чтобы применить новый язык
    }

    private fun saveSelectedLanguage(languageCode: String) {
        sharedPreferences.edit().putString("language", languageCode).apply()
    }

    private fun isNightModeEnabled(): Boolean {
        return sharedPreferences.getBoolean("nightMode", false)
    }

    private fun setNightModeEnabled(enabled: Boolean) {
        val nightMode = if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(nightMode)
        sharedPreferences.edit().putBoolean("nightMode", enabled).apply()
    }

    private fun showLogoutConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.sure_want_logout)
            .setPositiveButton(R.string.yes) { _, _ ->
                logout()
            }
            .setNegativeButton(R.string.no, null)
            .create()

        alertDialog.show()
    }

    private fun logout() {
        auth.signOut()
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val signInFragment = SignInFragment()
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, signInFragment)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

}
