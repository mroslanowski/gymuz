package com.example.gymuz.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.gymuz.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class AddWeightDialog(
    private val onWeightAdded: (Float, String?) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_weight, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etWeight = view.findViewById<TextInputEditText>(R.id.etWeight)
        val etNotes = view.findViewById<TextInputEditText>(R.id.etNotes)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        btnCancel.setOnClickListener { dismiss() }

        btnSave.setOnClickListener {
            val weightText = etWeight.text.toString()
            val notes = etNotes.text.toString()

            if (weightText.isBlank()) {
                etWeight.error = "Please enter weight"
                return@setOnClickListener
            }

            try {
                val weight = weightText.toFloat()
                if (weight <= 0 || weight > 500) {
                    etWeight.error = "Please enter a valid weight (1-500 kg)"
                    return@setOnClickListener
                }

                onWeightAdded(weight, notes.takeIf { it.isNotBlank() })
                dismiss()

            } catch (e: NumberFormatException) {
                etWeight.error = "Please enter a valid number"
            }
        }
    }

    companion object {
        const val TAG = "AddWeightDialog"
    }
}