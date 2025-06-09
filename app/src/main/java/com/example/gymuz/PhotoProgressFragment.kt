package com.example.gymuz.adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.gymuz.R
import com.example.gymuz.adapters.ProgressPhotoAdapter
import com.example.gymuz.database.entity.ProgressPhoto
import androidx.activity.result.contract.ActivityResultContracts

class PhotoProgressFragment : Fragment() {

    private val photos = mutableListOf<ProgressPhoto>()
    private lateinit var adapter: ProgressPhotoAdapter

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                photos.add(ProgressPhoto(userId = 0, photoPath = uri.toString(), type = "", date = "", description = ""))
                adapter.notifyItemInserted(photos.size - 1)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_photo_progress, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAddPhoto = view.findViewById<Button>(R.id.btnAddPhoto)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvProgressPhotos)
        adapter = ProgressPhotoAdapter(photos, onPhotoClick = {}, onDeleteClick = {})
        recyclerView.adapter = adapter

        btnAddPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }
    }
}