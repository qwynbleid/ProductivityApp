package com.example.myapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapp.MainActivity
import com.example.myapp.R
import com.example.myapp.databinding.FragmentSignInBinding
import com.example.myapp.ui.main.NotesFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding : FragmentSignInBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as MainActivity
        activity.showMenu(false)

        auth = FirebaseAuth.getInstance()
        binding = FragmentSignInBinding.bind(view)

        binding.apply {

            signUpBt.setOnClickListener {
                val signUpFragment = SignUpFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, signUpFragment)
                    .commit()
            }

            submitBt.setOnClickListener {

                val email = userEmail.editText?.text.toString()
                val password = userPassword.editText?.text.toString()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(),R.string.all_fields_should_be_filled, Toast.LENGTH_SHORT).show()
                }  else {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            checkEmailVerification()
                        } else {
                            val exception = task.exception
                            if (exception is FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(requireContext(), R.string.invalid_password, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), R.string.failed_to_sign_in, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkEmailVerification() {
        val fbUser = auth.currentUser

        if (fbUser != null) {
            if (fbUser.isEmailVerified) {
                Toast.makeText(requireContext(),R.string.logged_in, Toast.LENGTH_SHORT).show()
                val notesFragment = NotesFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, notesFragment)
                    .commit()
            } else {
                Toast.makeText(requireContext(), R.string.verify_your_email_first, Toast.LENGTH_SHORT)
                    .show()
                auth.signOut()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }
}