package com.example.gymuz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.lang.NumberFormatException

class AddExerciseBottomSheet(
    private val dayId: Int,
    private val onExerciseAdded: (String, Int, Int, Float) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pobierz referencje do elementów widoku
        val etExerciseName = view.findViewById<TextInputEditText>(R.id.etExerciseName)
        val etSets = view.findViewById<TextInputEditText>(R.id.etSets)
        val etReps = view.findViewById<TextInputEditText>(R.id.etReps)
        val etWeight = view.findViewById<TextInputEditText>(R.id.etWeight)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnAddExercise = view.findViewById<Button>(R.id.btnAddExercise)

        // Ustaw akcje przycisków
        btnCancel.setOnClickListener { dismiss() }

        btnAddExercise.setOnClickListener {
            val exerciseName = etExerciseName.text.toString()
            if (exerciseName.isBlank()) {
                etExerciseName.error = "Podaj nazwę ćwiczenia"
                return@setOnClickListener
            }

            try {
                val sets = etSets.text.toString().toInt()
                val reps = etReps.text.toString().toInt()
                val weight = etWeight.text.toString().toFloat()

                if (sets <= 0 || reps <= 0 || weight < 0) {
                    Snackbar.make(
                        view,
                        "Wszystkie wartości muszą być większe od zera",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // Przekaż dane za pomocą callbacka
                onExerciseAdded(exerciseName, sets, reps, weight)
                dismiss()

            } catch (e: NumberFormatException) {
                Snackbar.make(
                    view,
                    "Wprowadź poprawne wartości liczbowe",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        const val TAG = "AddExerciseBottomSheet"
    }
}