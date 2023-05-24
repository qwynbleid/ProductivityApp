package com.example.myapp

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.myapp.databinding.ActivityMainBinding
import com.example.myapp.ui.main.NotesFragment
import com.example.myapp.ui.login.SignInFragment
import com.example.myapp.ui.main.ExploreFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.bottomMenu.setOnItemSelectedListener {

                when(it.itemId) {
                    R.id.notes_item -> {
                        showNotesFragment()
                    }
                    R.id.explore_item -> {
                        showExploreFragment()
                    }
                }
                true
            }


        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

//        val splashFragment = SplashFragment()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragmentContainer, splashFragment)
//            .commit()

        if (auth.currentUser == null) {
            showLoginFragment()
        } else {
            showNotesFragment()
        }
    }

    fun showMenu(showMenu: Boolean) {
        if (showMenu) {
            binding.bottomMenu.visibility = View.VISIBLE
        } else {
            binding.bottomMenu.visibility = View.GONE
        }
    }



    private fun showExploreFragment() {
        val exploreFragment = ExploreFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, exploreFragment)
            .commit()
    }

    private fun showLoginFragment() {
        val loginFragment = SignInFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, loginFragment)
            .commit()
    }

    private fun showNotesFragment() {
        val notesFragment = NotesFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, notesFragment)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            val alertDialog = AlertDialog.Builder(this)
                .setMessage("Ви впевнені, що хочете вийти з додатку?")
                .setPositiveButton("Так") { _, _ ->
                    finish()
                }
                .setNegativeButton("Ні", null)
                .create()
            alertDialog.show()
        } else {
            super.onBackPressed()
        }
    }
}