package com.example.gymuz.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymuz.R
import com.example.gymuz.adapters.ProgressPhotoAdapter
import com.example.gymuz.database.AppDatabase
import com.example.gymuz.database.repository.ProgressRepository
import com.example.gymuz.dialogs.AddPhotoDialog
import com.example.gymuz.dialogs.PhotoViewDialog
import com.example.gymuz.login.UserPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PhotoProgressFragment : Fragment() {

    private lateinit var repository: ProgressRepository
    private lateinit var userPreferences: UserPreferences
    private var currentUserId: Int = -1

    private lateinit var btnAddPhoto: Button
    private lateinit var rvProgressPhotos: RecyclerView
    private lateinit var photoAdapter: ProgressPhotoAdapter

    private val CAMERA_PERMISSION_CODE = 101
    private val STORAGE_PERMISSION_CODE = 102

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeComponents(view)
        setupRepository()
        setupRecyclerView()
        setupClickListeners()
        loadPhotoData()
    }

    private fun initializeComponents(view: View) {
        btnAddPhoto = view.findViewById(R.id.btnAddPhoto)
        rvProgressPhotos = view.findViewById(R.id.rvProgressPhotos)
    }

    private fun setupRepository() {
        val db = AppDatabase.getDatabase(requireContext())
        repository = ProgressRepository(
            db.weightEntryDao(),
            db.exerciseProgressDao(),
            db.progressPhotoDao()
        )
        userPreferences = UserPreferences(requireContext())
        currentUserId = userPreferences.getUserId()

        if (currentUserId == -1) {
            Snackbar.make(requireView(), "User not logged in", Snackbar.LENGTH_LONG).show()
            return
        }
    }

    private fun setupRecyclerView() {
        photoAdapter = ProgressPhotoAdapter(
            mutableListOf(),
            onPhotoClick = { photo ->
                showPhotoDialog(photo)
            },
            onDeleteClick = { photo ->
                deletePhoto(photo)
            }
        )

        rvProgressPhotos.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = photoAdapter
        }
    }

    private fun setupClickListeners() {
        btnAddPhoto.setOnClickListener {
            showAddPhotoDialog()
        }
    }

    fun showAddPhotoDialog() {
        if (checkPermissions()) {
            val dialog = AddPhotoDialog { photoPath, type, description ->
                addPhoto(photoPath, type, description)
            }
            dialog.show(parentFragmentManager, AddPhotoDialog.TAG)
        } else {
            requestPermissions()
        }
    }

    private fun addPhoto(photoPath: String, type: String, description: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                repository.addProgressPhoto(currentUserId, photoPath, type, description)
                loadPhotoData()
                Snackbar.make(requireView(), "Photo added successfully", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Error adding photo: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun deletePhoto(photo: com.example.gymuz.database.entity.ProgressPhoto) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                repository.deleteProgressPhoto(photo)
                photoAdapter.removePhoto(photo)

                // Try to delete the actual file
                try {
                    val file = java.io.File(photo.photoPath)
                    if (file.exists()) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    // File deletion failed, but we'll continue
                }

                Snackbar.make(requireView(), "Photo deleted", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Error deleting photo: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showPhotoDialog(photo: com.example.gymuz.database.entity.ProgressPhoto) {
        val dialog = PhotoViewDialog(photo)
        dialog.show(parentFragmentManager, PhotoViewDialog.TAG)
    }

    private fun loadPhotoData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val photos = repository.getProgressPhotos(currentUserId)
                photoAdapter.updatePhotos(photos)
            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Error loading photos: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val storagePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return cameraPermission && storagePermission
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions,
            CAMERA_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    showAddPhotoDialog()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Camera and storage permissions are required to add photos",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}