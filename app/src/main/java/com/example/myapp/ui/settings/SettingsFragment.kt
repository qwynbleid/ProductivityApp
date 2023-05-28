package com.example.myapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myapp.MainActivity
import com.example.myapp.R
import com.example.myapp.databinding.FragmentSettingsBinding
import com.example.myapp.ui.login.SignInFragment
import com.example.myapp.utils.ThemeUtils
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment: Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val themeSwitch: Switch = view.findViewById(R.id.themeSwitch)
        themeSwitch.isChecked = ThemeUtils.getCurrentThemeMode(requireContext()) == ThemeUtils.THEME_DARK

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ThemeUtils.setCurrentThemeMode(requireContext(), ThemeUtils.THEME_DARK)
            } else {
                ThemeUtils.setCurrentThemeMode(requireContext(), ThemeUtils.THEME_LIGHT)
            }
            applyCurrentTheme() // Применить новую тему
        }

        val activity = requireActivity() as MainActivity
        activity.showMenu(false)

        binding = FragmentSettingsBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        binding.apply {
            logoutButton.setOnClickListener {

                auth.signOut()

                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                val signInFragment = SignInFragment()
                fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, signInFragment)
                    .commit()

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        applyCurrentTheme() // Применение текущей темы при возврате к фрагменту настроек
    }

    private fun applyCurrentTheme() {
        val currentThemeMode = ThemeUtils.getCurrentThemeMode(requireContext())
        if (currentThemeMode == ThemeUtils.THEME_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


}
