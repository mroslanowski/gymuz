package com.example.gymuz.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gymuz.R
import com.example.gymuz.database.AppDatabase
import com.example.gymuz.database.entity.User
import com.example.gymuz.database.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var userDao: UserDao
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicjalizacja UserPreferences
        userPreferences = UserPreferences(this)

        val nameEditText = findViewById<EditText>(R.id.etName)
        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.etConfirmPassword)
        val registerButton = findViewById<Button>(R.id.signUpButton)

        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Walidacja danych
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Hasła się nie zgadzają", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val existingUser = withContext(Dispatchers.IO) {
                    userDao.getUserByEmail(email)
                }

                if (existingUser != null) {
                    Toast.makeText(this@RegisterActivity, "Email już istnieje", Toast.LENGTH_SHORT).show()
                } else {
                    withContext(Dispatchers.IO) {
                        // Zapisanie użytkownika do bazy danych
                        userDao.insertUser(User(name = name, email = email, password = password))

                        // Pobierz użytkownika po emailu aby uzyskać jego ID
                        val newUser = userDao.getUserByEmail(email)
                        if (newUser != null) {
                            // Opcjonalnie - jeśli chcesz od razu zalogować użytkownika po rejestracji
                            userPreferences.setLoggedIn(true)
                            userPreferences.saveUserEmail(email)
                            userPreferences.saveUserId(newUser.id)
                            userPreferences.saveUserName(name)
                        }
                    }

                    Toast.makeText(this@RegisterActivity, "Rejestracja udana!", Toast.LENGTH_SHORT).show()

                    // Przejdź do LoginActivity lub bezpośrednio do MainActivity jeśli użytkownik został zalogowany
                    if (userPreferences.isLoggedIn()) {
                        startActivity(Intent(this@RegisterActivity, com.example.gymuz.MainActivity::class.java))
                    } else {
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    }
                    finish()
                }
            }
        }
    }
}