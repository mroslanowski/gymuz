package com.example.gymuz.dialogs

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.gymuz.R
import com.example.gymuz.database.entity.ProgressPhoto
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PhotoViewDialog(
    private val photo: ProgressPhoto
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_photo_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivPhoto = view.findViewById<ImageView>(R.id.ivPhoto)
        val tvType = view.findViewById<TextView>(R.id.tvType)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)

        // Load and display the photo
        try {
            val file = File(photo.photoPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(photo.photoPath)
                if (bitmap != null) {
                    ivPhoto.setImageBitmap(bitmap)
                } else {
                    ivPhoto.setImageResource(R.drawable.ic_fitness)
                }
            } else {
                ivPhoto.setImageResource(R.drawable.ic_fitness)
            }
        } catch (e: Exception) {
            ivPhoto.setImageResource(R.drawable.ic_fitness)
        }

        // Set photo details
        tvType.text = photo.type
        tvDate.text = formatDate(photo.date)

        if (photo.description.isNullOrBlank()) {
            tvDescription.visibility = View.GONE
        } else {
            tvDescription.visibility = View.VISIBLE
            tvDescription.text = photo.description
        }

        // Make dialog dismissible by clicking outside
        ivPhoto.setOnClickListener {
            dismiss()
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        const val TAG = "PhotoViewDialog"
    }
}