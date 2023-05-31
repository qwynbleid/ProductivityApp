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
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding : FragmentSignUpBinding
    private val signInFragment = SignInFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        binding = FragmentSignUpBinding.bind(view)

        binding.apply {

            returnToSignIn.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, signInFragment)
                    .commit()
            }

            submitBt.setOnClickListener {
                val email = userEmail.editText?.text.toString()
                val password = userPassword.editText?.text.toString()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(),R.string.all_fields_should_be_filled, Toast.LENGTH_SHORT).show()
                } else if (password.length < 8) {
                    Toast.makeText(requireContext(),R.string.password_is_too_short, Toast.LENGTH_SHORT).show()
                } else {

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        try {
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), R.string.registration_successful, Toast.LENGTH_SHORT).show()

                                sendEmailVerification()

                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainer, signInFragment)
                                    .commit()

                                auth.signOut()

                            } else {
                                throw task.exception!!
                            }
                        } catch (e: FirebaseAuthUserCollisionException) {
                            Toast.makeText(requireContext(), R.string.email_address_is_already_in_use, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), R.string.failed_to_register, Toast.LENGTH_SHORT).show()
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
                    R.string.verification_email_is_sent_verify_it_and_log_in_again,
                    Toast.LENGTH_SHORT
                ).show()

            }
        } else {
            Toast.makeText(
                context,
                R.string.failed_to_send_verification_email,
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