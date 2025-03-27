package com.example.gymuz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gymuz.database.AppDatabase
import com.example.gymuz.database.User
import com.example.gymuz.database.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

            if (password != confirmPassword) {
                Toast.makeText(this, "Hasła się nie zgadzają", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // Sprawdzanie, czy użytkownik już istnieje w bazie
                val existingUser = withContext(Dispatchers.IO) {
                    userDao.getUserByEmail(email)
                }

                if (existingUser != null) {
                    Toast.makeText(this@RegisterActivity, "Email już istnieje", Toast.LENGTH_SHORT).show()
                } else {
                    // Dodawanie nowego użytkownika do bazy
                    withContext(Dispatchers.IO) {
                        userDao.insertUser(User(name = name, email = email, password = password))
                    }

                    Toast.makeText(this@RegisterActivity, "Rejestracja udana!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}
