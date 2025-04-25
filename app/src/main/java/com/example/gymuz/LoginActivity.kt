package com.example.gymuz

import android.content.Intent
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
    private lateinit var userPreferences: UserPreferences
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicjalizacja UserPreferences
        userPreferences = UserPreferences(this)

        // Inicjalizacja bazy danych
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Sprawdzenie, czy użytkownik jest już zalogowany
        if (userPreferences.isLoggedIn()) {
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
                    // Jeśli dane logowania są poprawne, zapisz status zalogowania i email w UserPreferences
                    userPreferences.setLoggedIn(true)
                    userPreferences.saveUserEmail(email)
                    userPreferences.saveUserId(user.id) // Zapisanie ID użytkownika

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