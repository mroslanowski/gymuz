package com.example.gymuz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymuz.R
import com.example.gymuz.database.entity.WeightEntry
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class WeightEntryAdapter(
    private var entries: MutableList<WeightEntry>,
    private val onDeleteClick: (WeightEntry) -> Unit
) : RecyclerView.Adapter<WeightEntryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvWeight: TextView = view.findViewById(R.id.tvWeight)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvNotes: TextView = view.findViewById(R.id.tvNotes)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weight_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        val decimalFormat = DecimalFormat("#.#")

        holder.tvWeight.text = "${decimalFormat.format(entry.weight)} kg"
        holder.tvDate.text = formatDate(entry.date)

        if (entry.notes.isNullOrBlank()) {
            holder.tvNotes.visibility = View.GONE
        } else {
            holder.tvNotes.visibility = View.VISIBLE
            holder.tvNotes.text = entry.notes
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(entry)
        }
    }

    override fun getItemCount() = entries.size

    fun updateEntries(newEntries: List<WeightEntry>) {
        entries.clear()
        entries.addAll(newEntries)
        notifyDataSetChanged()
    }

    fun removeEntry(entry: WeightEntry) {
        val position = entries.indexOf(entry)
        if (position != -1) {
            entries.removeAt(position)
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