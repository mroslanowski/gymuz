package com.example.gymuz

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gymuz.database.AppDatabase
import com.example.gymuz.database.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicjalizacja SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // Inicjalizacja bazy danych
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Sprawdzenie, czy użytkownik jest już zalogowany
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Obsługa przycisku logowania
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            lifecycleScope.launch {
                // Sprawdzanie danych użytkownika w bazie
                val user = withContext(Dispatchers.IO) {
                    userDao.getUserByEmail(email)
                }

                if (user != null && user.password == password) {
                    // Jeśli dane logowania są poprawne, zapisz status zalogowania w SharedPreferences
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                    // Przejście do głównej aktywności
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // Jeśli dane logowania są niepoprawne, wyświetl komunikat
                    Toast.makeText(this@LoginActivity, "Błędne dane logowania", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Obsługa przycisku rejestracji
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
