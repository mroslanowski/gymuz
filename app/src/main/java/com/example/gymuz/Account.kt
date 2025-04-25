package com.example.gymuz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gymuz.database.AppDatabase
import com.example.gymuz.database.User
import com.example.gymuz.database.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Account : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var sexTextView: TextView
    private lateinit var userDao: UserDao
    private lateinit var userPreferences: UserPreferences
    private var userEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        nameTextView = view.findViewById(R.id.Name)
        emailTextView = view.findViewById(R.id.Email)
        passwordTextView = view.findViewById(R.id.password)
        sexTextView = view.findViewById(R.id.Sex)

        val db = AppDatabase.getDatabase(requireContext())
        userDao = db.userDao()

        // Inicjalizacja UserPreferences i pobranie emaila użytkownika
        userPreferences = UserPreferences(requireContext())
        userEmail = userPreferences.getUserEmail()

        if (userEmail != null) {
            loadUserData(userEmail!!)
        } else {
            Toast.makeText(requireContext(), "Błąd: Brak zalogowanego użytkownika", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.editButton).setOnClickListener {
            showEditDialog()
        }

        return view
    }

    private fun loadUserData(email: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val user: User? = userDao.getUserByEmail(email)
            withContext(Dispatchers.Main) {
                if (user != null) {
                    nameTextView.text = user.name
                    emailTextView.text = user.email
                    passwordTextView.text = user.password
                    sexTextView.text = user.sex ?: "" // Dodane zabezpieczenie przed nullem
                } else {
                    Toast.makeText(requireContext(), "Błąd: Nie znaleziono użytkownika", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showEditDialog() {
        val bottomSheet = AccountEdit(
            nameTextView.text.toString(),
            emailTextView.text.toString(),
            passwordTextView.text.toString(),
            sexTextView.text.toString()
        ) { newName, newEmail, newPassword, newSex ->
            updateUserData(newName, newEmail, newPassword, newSex)
        }
        bottomSheet.show(parentFragmentManager, "EditAccount")
    }

    private fun updateUserData(newName: String, newEmail: String, newPassword: String, newSex: String) {
        if (userEmail == null) return

        lifecycleScope.launch(Dispatchers.IO) {
            val user = userDao.getUserByEmail(userEmail!!)
            if (user != null) {
                // Aktualizacja danych użytkownika w bazie danych
                user.name = newName
                user.email = newEmail
                user.password = newPassword
                user.sex = newSex
                userDao.updateUser(user)

                // Aktualizacja emaila w UserPreferences jeśli został zmieniony
                if (userEmail != newEmail) {
                    withContext(Dispatchers.Main) {
                        userPreferences.saveUserEmail(newEmail)
                        userEmail = newEmail
                    }
                }

                // Aktualizacja widoku
                withContext(Dispatchers.Main) {
                    nameTextView.text = newName
                    emailTextView.text = newEmail
                    passwordTextView.text = newPassword
                    sexTextView.text = newSex
                    Toast.makeText(requireContext(), "Dane zostały zaktualizowane", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}