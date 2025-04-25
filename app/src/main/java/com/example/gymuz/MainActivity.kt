package com.example.gymuz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.gymuz.database.AppDatabase


import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        drawerLayout = findViewById(R.id.drawer_layout)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Home()).commit()
            navigationView.setCheckedItem(R.id.bottom_home)
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> replaceFragment(Home())
                R.id.bottom_qr_code -> replaceFragment(QR_Code())
                R.id.bottom_create_plan -> replaceFragment(Create_Plan())
                R.id.bottom_plan -> replaceFragment(Plan())
                R.id.bottom_account -> replaceFragment(Account())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_gym_map -> replaceFragment(GymMap())
            R.id.nav_pdf_generator -> replaceFragment(PDF_Generator())
            R.id.nav_settings -> replaceFragment(Settings())
            R.id.nav_logout -> logoutUser()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logoutUser() {
        // Czyszczenie flagi logowania
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

        // Komunikat informacyjny
        Toast.makeText(this, "Wylogowano!", Toast.LENGTH_SHORT).show()

        // Przejście do ekranu logowania
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Zamknięcie MainActivity
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
