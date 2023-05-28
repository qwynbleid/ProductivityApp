package com.example.myapp.ui.settings

import android.app.AlertDialog
import android.content.Context
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

class SettingsFragment: Fragment(R.layout.fragment_settings) {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

}
