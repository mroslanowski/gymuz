package com.example.gymuz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.gymuz.database.AppDatabase
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountEdit(
    private val currentName: String,
    private val currentEmail: String,
    private val currentPassword: String,
    private val currentSex: String,
    private val onSave: (String, String, String, String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var sexEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_account_edit, container, false)

        nameEditText = view.findViewById(R.id.name)
        emailEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.password)
        sexEditText = view.findViewById(R.id.sex)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        // Wypełniamy pola aktualnymi danymi
        nameEditText.setText(currentName)
        emailEditText.setText(currentEmail)
        passwordEditText.setText(currentPassword)
        sexEditText.setText(currentSex)

        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString()
            val newEmail = emailEditText.text.toString()
            val newPassword = passwordEditText.text.toString()
            val newSex = sexEditText.text.toString()

            // Walidacja wprowadzonych danych
            if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(context, "Wszystkie pola muszą być wypełnione", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Walidacja emaila
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(context, "Nieprawidłowy format adresu email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sprawdzenie czy email się zmienił i czy nie jest już zajęty przez innego użytkownika
            if (newEmail != currentEmail) {
                lifecycleScope.launch {
                    val userDao = AppDatabase.getDatabase(requireContext()).userDao()
                    val existingUser = withContext(Dispatchers.IO) {
                        userDao.getUserByEmail(newEmail)
                    }

                    if (existingUser != null && existingUser.email != currentEmail) {
                        Toast.makeText(context, "Ten adres email jest już zajęty", Toast.LENGTH_SHORT).show()
                    } else {
                        // Zapisz zmiany i zamknij dialog
                        onSave(newName, newEmail, newPassword, newSex)
                        dismiss()
                    }
                }
            } else {
                // Jeśli email się nie zmienił, zapisz zmiany i zamknij dialog
                onSave(newName, newEmail, newPassword, newSex)
                dismiss()
            }
        }

        return view
    }
}