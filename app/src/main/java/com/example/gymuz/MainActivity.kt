package com.example.gymuz

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.gymuz.database.AppDatabase
import com.example.gymuz.Create_Plan
import com.example.gymuz.GymMap
import com.example.gymuz.fragments.Plan
import com.example.gymuz.Progress
import com.example.gymuz.PdfGenerator
import com.example.gymuz.Settings
import com.example.gymuz.login.Account
import com.example.gymuz.login.LoginActivity
import com.example.gymuz.login.UserPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navigationView: NavigationView
    private lateinit var db: AppDatabase
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPrefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userPreferences = UserPreferences(this)
        db = AppDatabase.getDatabase(this)

        drawerLayout = findViewById(R.id.drawer_layout)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Create_Plan()).commit()
            navigationView.setCheckedItem(R.id.bottom_home)
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> replaceFragment(Create_Plan())
                R.id.bottom_create_plan -> replaceFragment(Plan())
                R.id.nav_gym_map -> replaceFragment(GymMap())
                R.id.bottom_progress -> replaceFragment(Progress())
                R.id.bottom_account -> replaceFragment(Account())
            }
            true
        }

        updateNavigationHeader()
    }

    private fun updateNavigationHeader() {
        val headerView = navigationView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.nameTextView)
        val emailTextView = headerView.findViewById<TextView>(R.id.emailTextView)

        if (userPreferences.isLoggedIn()) {
            val userId = userPreferences.getUserId()

            if (userId != -1) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val user = db.userDao().getUserById(userId)

                        withContext(Dispatchers.Main) {
                            if (user != null) {
                                nameTextView.text = user.name ?: "Użytkownik GymUZ"
                                emailTextView.text = user.email ?: "gymuz@uz.zgora.pl"
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            val email = userPreferences.getUserEmail() ?: "gymuz@uz.zgora.pl"
                            val name = userPreferences.getUserName() ?: "Użytkownik GymUZ"

                            nameTextView.text = name
                            emailTextView.text = email
                        }
                    }
                }
            } else {
                val email = userPreferences.getUserEmail() ?: "gymuz@uz.zgora.pl"
                val name = userPreferences.getUserName() ?: "Użytkownik GymUZ"

                nameTextView.text = name
                emailTextView.text = email
            }
        } else {
            nameTextView.text = "Average GymUZApp Enjoyer"
            emailTextView.text = "GymUZ@uz.zgora.pl"
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_pdf_generator -> replaceFragment(PdfGenerator())
            R.id.nav_settings -> replaceFragment(Settings())
            R.id.nav_logout -> logoutUser()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logoutUser() {
        userPreferences.clearUserData()

        val headerView = navigationView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.nameTextView)
        val emailTextView = headerView.findViewById<TextView>(R.id.emailTextView)

        nameTextView.text = "Average GymUZApp Enjoyer"
        emailTextView.text = "GymUZ@uz.zgora.pl"

        Toast.makeText(this, "Wylogowano!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        updateNavigationHeader()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}