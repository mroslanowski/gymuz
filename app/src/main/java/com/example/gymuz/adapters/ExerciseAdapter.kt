package com.example.gymuz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymuz.R
import com.example.gymuz.database.entity.Exercise
import java.text.DecimalFormat

class ExerciseAdapter(
    private var exercises: MutableList<Exercise>,
    private val onExerciseUpdate: (Exercise) -> Unit,
    private val onExerciseDelete: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvExerciseName: TextView = view.findViewById(R.id.tvExerciseName)
        val tvSets: TextView = view.findViewById(R.id.tvSets)
        val tvReps: TextView = view.findViewById(R.id.tvReps)
        val tvWeight: TextView = view.findViewById(R.id.tvWeight)
        val btnDecreaseSets: ImageButton = view.findViewById(R.id.btnDecreaseSets)
        val btnIncreaseSets: ImageButton = view.findViewById(R.id.btnIncreaseSets)
        val btnDecreaseReps: ImageButton = view.findViewById(R.id.btnDecreaseReps)
        val btnIncreaseReps: ImageButton = view.findViewById(R.id.btnIncreaseReps)
        val btnDecreaseWeight: ImageButton = view.findViewById(R.id.btnDecreaseWeight)
        val btnIncreaseWeight: ImageButton = view.findViewById(R.id.btnIncreaseWeight)
        val btnDeleteExercise: ImageButton = view.findViewById(R.id.btnDeleteExercise)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        val decimalFormat = DecimalFormat("#.#")

        holder.tvExerciseName.text = exercise.name
        holder.tvSets.text = exercise.sets.toString()
        holder.tvReps.text = exercise.reps.toString()
        holder.tvWeight.text = decimalFormat.format(exercise.weight)

        // Przyciski do serii
        holder.btnDecreaseSets.setOnClickListener {
            if (exercise.sets > 1) {
                val updatedExercise = exercise.copy(sets = exercise.sets - 1)
                updateExercise(position, updatedExercise)
            }
        }

        holder.btnIncreaseSets.setOnClickListener {
            val updatedExercise = exercise.copy(sets = exercise.sets + 1)
            updateExercise(position, updatedExercise)
        }

        // Przyciski do powtórzeń
        holder.btnDecreaseReps.setOnClickListener {
            if (exercise.reps > 1) {
                val updatedExercise = exercise.copy(reps = exercise.reps - 1)
                updateExercise(position, updatedExercise)
            }
        }

        holder.btnIncreaseReps.setOnClickListener {
            val updatedExercise = exercise.copy(reps = exercise.reps + 1)
            updateExercise(position, updatedExercise)
        }

        // Przyciski do ciężaru
        holder.btnDecreaseWeight.setOnClickListener {
            if (exercise.weight >= 2.5f) {
                val updatedExercise = exercise.copy(weight = exercise.weight - 2.5f)
                updateExercise(position, updatedExercise)
            } else if (exercise.weight > 0) {
                val updatedExercise = exercise.copy(weight = 0f)
                updateExercise(position, updatedExercise)
            }
        }

        holder.btnIncreaseWeight.setOnClickListener {
            val updatedExercise = exercise.copy(weight = exercise.weight + 2.5f)
            updateExercise(position, updatedExercise)
        }

        // Przycisk usuwania
        holder.btnDeleteExercise.setOnClickListener {
            onExerciseDelete(exercise)
            exercises.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, exercises.size)
        }
    }

    override fun getItemCount() = exercises.size

    private fun updateExercise(position: Int, exercise: Exercise) {
        exercises[position] = exercise
        notifyItemChanged(position)
        onExerciseUpdate(exercise)
    }

    fun updateExercises(newExercises: List<Exercise>) {
        exercises.clear()
        exercises.addAll(newExercises)
        notifyDataSetChanged()
    }

    fun addExercise(exercise: Exercise) {
        exercises.add(exercise)
        notifyItemInserted(exercises.size - 1)
    }
}