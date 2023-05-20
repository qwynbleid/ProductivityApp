package com.example.myapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapp.R
import com.example.myapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding : FragmentSignUpBinding
    private val signInFragment = SignInFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        auth = FirebaseAuth.getInstance()
        binding = FragmentSignUpBinding.bind(view)

        binding.apply {

            cancelBt.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, signInFragment)
                    .commit()
            }

            submitBt.setOnClickListener {
                val email = userEmail.text.toString().trim()
                val password = userPassword.text.toString().trim()

                if (userEmail.text.isEmpty() || userPassword.text.isEmpty()) {
                    Toast.makeText(requireContext(),"All fields should be filled", Toast.LENGTH_SHORT).show()
                } else if (password.length < 8) {
                    Toast.makeText(requireContext(),"Password is too short", Toast.LENGTH_SHORT).show()
                } else {

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(requireContext(),"Registration Successful", Toast.LENGTH_SHORT).show()

                            sendEmailVerification()

                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, signInFragment)
                                .commit()

                            auth.signOut()

                        } else {
                            Toast.makeText(requireContext(),"Failed to register", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun sendEmailVerification() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val context = context

        if (firebaseUser != null && context != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener {
                Toast.makeText(
                    context,
                    "Verification Email is sent, verify it and log in again",
                    Toast.LENGTH_SHORT
                ).show()

            }
        } else {
            Toast.makeText(
                context,
                "Failed to send verification Email",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        binding = FragmentSignUpBinding.bind(view)
        return view
    }
}