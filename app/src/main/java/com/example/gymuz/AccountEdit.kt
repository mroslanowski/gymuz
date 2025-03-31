package com.example.gymuz

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;



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
            onSave(
                nameEditText.text.toString(),
                emailEditText.text.toString(),
                passwordEditText.text.toString(),
                sexEditText.text.toString()
            )
            dismiss()
        }

        return view
    }
}
