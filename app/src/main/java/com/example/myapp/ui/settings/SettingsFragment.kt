package com.example.myapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

}