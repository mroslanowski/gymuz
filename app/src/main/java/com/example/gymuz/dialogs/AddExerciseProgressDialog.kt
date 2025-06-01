package com.example.gymuz.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.gymuz.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class AddExerciseProgressDialog(
    private val onExerciseProgressAdded: (String, Float, Int, Int, String?) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_exercise_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etExerciseName = view.findViewById<TextInputEditText>(R.id.etExerciseName)
        val etWeight = view.findViewById<TextInputEditText>(R.id.etWeight)
        val etSets = view.findViewById<TextInputEditText>(R.id.etSets)
        val etReps = view.findViewById<TextInputEditText>(R.id.etReps)
        val etNotes = view.findViewById<TextInputEditText>(R.id.etNotes)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        btnCancel.setOnClickListener { dismiss() }

        btnSave.setOnClickListener {
            val exerciseName = etExerciseName.text.toString().trim()
            val weightText = etWeight.text.toString()
            val setsText = etSets.text.toString()
            val repsText = etReps.text.toString()
            val notes = etNotes.text.toString()

            // Validation
            if (exerciseName.isBlank()) {
                etExerciseName.error = "Please enter exercise name"
                return@setOnClickListener
            }

            if (weightText.isBlank()) {
                etWeight.error = "Please enter weight"
                return@setOnClickListener
            }

            if (setsText.isBlank()) {
                etSets.error = "Please enter number of sets"
                return@setOnClickListener
            }

            if (repsText.isBlank()) {
                etReps.error = "Please enter number of reps"
                return@setOnClickListener
            }

            try {
                val weight = weightText.toFloat()
                val sets = setsText.toInt()
                val reps = repsText.toInt()

                if (weight < 0 || weight > 1000) {
                    etWeight.error = "Please enter a valid weight (0-1000 kg)"
                    return@setOnClickListener
                }

                if (sets <= 0 || sets > 100) {
                    etSets.error = "Please enter valid number of sets (1-100)"
                    return@setOnClickListener
                }

                if (reps <= 0 || reps > 1000) {
                    etReps.error = "Please enter valid number of reps (1-1000)"
                    return@setOnClickListener
                }

                onExerciseProgressAdded(
                    exerciseName,
                    weight,
                    sets,
                    reps,
                    notes.takeIf { it.isNotBlank() }
                )
                dismiss()

            } catch (e: NumberFormatException) {
                view.let {
                    com.google.android.material.snackbar.Snackbar.make(
                        it,
                        "Please enter valid numbers",
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    companion object {
        const val TAG = "AddExerciseProgressDialog"
    }
}