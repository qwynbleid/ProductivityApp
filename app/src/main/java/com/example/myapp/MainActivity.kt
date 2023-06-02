package com.example.myapp

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.example.myapp.databinding.ActivityMainBinding
import com.example.myapp.ui.main.NotesFragment
import com.example.myapp.ui.login.SignInFragment
import com.example.myapp.ui.main.ExploreFragment
import com.example.myapp.ui.settings.SettingsFragment
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("Language", Context.MODE_PRIVATE)

        if (savedInstanceState == null) {
            val settingsFragment = SettingsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, settingsFragment)
                .commit()
        }

        binding.bottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.notes_item -> showNotesFragment()
                R.id.explore_item -> showExploreFragment()
            }
            true
        }

        if (auth.currentUser == null) {
            showLoginFragment()
        } else {
            showNotesFragment()
        }
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        val langSharedPreferences = getSharedPreferences("Language", Context.MODE_PRIVATE)
        val savedLanguage = langSharedPreferences.getString("language", "")

        if (savedLanguage != null && savedLanguage.isNotEmpty()) {
            setLocale(savedLanguage)
        }
        return super.onCreateView(parent, name, context, attrs)
    }

    fun showMenu(showMenu: Boolean) {
        binding.bottomMenu.visibility = if (showMenu) View.VISIBLE else View.GONE
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
                .setMessage(R.string.sure_want_quit)
                .setPositiveButton(R.string.yes) { _, _ ->
                    finish()
                }
                .setNegativeButton(R.string.no, null)
                .create()
            alertDialog.show()
        } else {
            super.onBackPressed()
        }
    }


    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = resources.configuration
        configuration.setLocale(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)

        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
    }
}
