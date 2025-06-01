package com.example.gymuz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymuz.R
import com.example.gymuz.database.entity.ExerciseProgress
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ExerciseProgressAdapter(
    private var progressList: MutableList<ExerciseProgress>,
    private val onDeleteClick: (ExerciseProgress) -> Unit
) : RecyclerView.Adapter<ExerciseProgressAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvExerciseName: TextView = view.findViewById(R.id.tvExerciseName)
        val tvWeight: TextView = view.findViewById(R.id.tvWeight)
        val tvSetsReps: TextView = view.findViewById(R.id.tvSetsReps)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvNotes: TextView = view.findViewById(R.id.tvNotes)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_progress, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val progress = progressList[position]
        val decimalFormat = DecimalFormat("#.#")

        holder.tvExerciseName.text = progress.exerciseName
        holder.tvWeight.text = "${decimalFormat.format(progress.weight)} kg"
        holder.tvSetsReps.text = "${progress.sets} sets × ${progress.reps} reps"
        holder.tvDate.text = formatDate(progress.date)

        if (progress.notes.isNullOrBlank()) {
            holder.tvNotes.visibility = View.GONE
        } else {
            holder.tvNotes.visibility = View.VISIBLE
            holder.tvNotes.text = progress.notes
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(progress)
        }
    }

    override fun getItemCount() = progressList.size

    fun updateProgress(newProgress: List<ExerciseProgress>) {
        progressList.clear()
        progressList.addAll(newProgress)
        notifyDataSetChanged()
    }

    fun removeProgress(progress: ExerciseProgress) {
        val position = progressList.indexOf(progress)
        if (position != -1) {
            progressList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
}