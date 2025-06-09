package com.example.gymuz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gymuz.R
import com.example.gymuz.database.entity.ProgressPhoto
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProgressPhotoAdapter(
    private var photos: MutableList<ProgressPhoto>,
    private val onPhotoClick: (ProgressPhoto) -> Unit,
    private val onDeleteClick: (ProgressPhoto) -> Unit
) : RecyclerView.Adapter<ProgressPhotoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPhoto: ImageView = view.findViewById(R.id.ivPhoto)
        val tvType: TextView = view.findViewById(R.id.tvType)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_progress_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = photos[position]
        val file = File(photo.photoPath)
        if (file.exists()) {
            Glide.with(holder.ivPhoto.context)
                .load(file)
                .placeholder(R.drawable.ic_fitness)
                .error(R.drawable.ic_fitness)
                .into(holder.ivPhoto)
        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_fitness)
        }

        holder.tvType.text = photo.type
        holder.tvDate.text = formatDate(photo.date)

        if (photo.description.isNullOrBlank()) {
            holder.tvDescription.visibility = View.GONE
        } else {
            holder.tvDescription.visibility = View.VISIBLE
            holder.tvDescription.text = photo.description
        }

        holder.ivPhoto.setOnClickListener {
            onPhotoClick(photo)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(photo)
        }
    }

    override fun getItemCount() = photos.size

    fun updatePhotos(newPhotos: List<ProgressPhoto>) {
        photos.clear()
        photos.addAll(newPhotos)
        notifyDataSetChanged()
    }

    fun removePhoto(photo: ProgressPhoto) {
        val position = photos.indexOf(photo)
        if (position != -1) {
            photos.removeAt(position)
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