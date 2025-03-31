package com.example.gymuz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment


class Account: Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var sexTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        nameTextView = view.findViewById(R.id.Name)
        emailTextView = view.findViewById(R.id.Email)
        passwordTextView = view.findViewById(R.id.password)
        sexTextView = view.findViewById(R.id.Sex)

        view.findViewById<Button>(R.id.editButton).setOnClickListener {
            showEditDialog()
        }

        return view
    }

    private fun showEditDialog() {
        val bottomSheet = AccountEdit(
            nameTextView.text.toString(),
            emailTextView.text.toString(),
            passwordTextView.text.toString(),
            sexTextView.text.toString()
        ) { newName, newEmail, newPassword, newSex ->
            nameTextView.text = newName
            emailTextView.text = newEmail
            passwordTextView.text = newPassword
            sexTextView.text = newSex
        }
        bottomSheet.show(parentFragmentManager, "EditAccount")
    }
}
