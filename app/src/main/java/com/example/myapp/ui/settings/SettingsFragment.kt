package com.example.myapp.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
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

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as MainActivity
        activity.showMenu(false)

        binding = FragmentSettingsBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        val sharedPreferences = requireContext().getSharedPreferences("Mode", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val nightMode = sharedPreferences.getBoolean("night", false)

        if (nightMode) {
            binding.themeSwitch.isChecked = true
        }

        binding.languageButton.setOnClickListener {
            showLanguageSelectionDialog()
        }

        binding.apply {
            themeSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    editor.putBoolean("night", false)
                    editor.apply()
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    editor.putBoolean("night", true)
                    editor.apply()
                }
            }

            logoutButton.setOnClickListener {

                val alertDialog = AlertDialog.Builder(requireContext())
                    .setMessage(R.string.sure_want_logout)
                    .setPositiveButton(R.string.yes) { _, _ ->

                        auth.signOut()

                        val fragmentManager = requireActivity().supportFragmentManager
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                        val signInFragment = SignInFragment()
                        fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, signInFragment)
                            .commit()

                    }
                    .setNegativeButton(R.string.no, null)
                    .create()
                alertDialog.show()
            }
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)

        requireActivity().apply {
            baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
            recreate()
        }
    }

    private fun changeLanguage(languageCode: String) {
        val sharedPreferences = requireContext().getSharedPreferences("Language", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("language", languageCode)
        editor.apply()

        setLocale(languageCode)

        val fragmentManager = requireActivity().supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainer)
        val newFragment = currentFragment?.let { SettingsFragment() }
        if (newFragment != null) {
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, newFragment)
                .commit()
        }
    }

    private fun showLanguageSelectionDialog() {
        val languageOptions = arrayOf("Українська", "English")
        val currentLanguageIndex = if (isLanguageUkrainian()) 0 else 1

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.change_language)
            .setSingleChoiceItems(languageOptions, currentLanguageIndex) { dialog, which ->

                val selectedLanguage = when (which) {
                    0 -> "uk"
                    1 -> "en"
                    else -> "uk"
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

}