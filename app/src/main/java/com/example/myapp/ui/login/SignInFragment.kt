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

                val email = userEmail.text.toString().trim()
                val password = userPassword.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(),"All fields should be filled", Toast.LENGTH_SHORT).show()
                }  else {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            checkEmailVerification()
                        } else {
                            Toast.makeText(requireContext(),"Account does not exist", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(),"Logged in", Toast.LENGTH_SHORT).show()
                val notesFragment = NotesFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, notesFragment)
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Verify your email first", Toast.LENGTH_SHORT)
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