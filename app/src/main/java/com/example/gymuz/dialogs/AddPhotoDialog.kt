package com.example.gymuz.dialogs

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.gymuz.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoDialog(
    private val onPhotoAdded: (String, String, String?) -> Unit
) : BottomSheetDialogFragment() {

    private var currentPhotoPath: String = ""

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Photo was taken successfully
            savePhotoEntry()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data?.data?.let { uri ->
                // Copy the selected image to our app directory
                copyImageToAppDirectory(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rgPhotoType = view.findViewById<RadioGroup>(R.id.rgPhotoType)
        val etDescription = view.findViewById<TextInputEditText>(R.id.etDescription)
        val btnTakePhoto = view.findViewById<Button>(R.id.btnTakePhoto)
        val btnPickPhoto = view.findViewById<Button>(R.id.btnPickPhoto)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener { dismiss() }

        btnTakePhoto.setOnClickListener {
            takePhoto()
        }

        btnPickPhoto.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Snackbar.make(requireView(), "Error creating image file", Snackbar.LENGTH_SHORT).show()
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.gymuz.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun copyImageToAppDirectory(sourceUri: Uri) {
        try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val destinationFile = File(storageDir, "JPEG_${timeStamp}_.jpg")

            requireContext().contentResolver.openInputStream(sourceUri)?.use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            currentPhotoPath = destinationFile.absolutePath
            savePhotoEntry()

        } catch (e: Exception) {
            Snackbar.make(requireView(), "Error copying image: ${e.message}", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun savePhotoEntry() {
        val view = requireView()
        val rgPhotoType = view.findViewById<RadioGroup>(R.id.rgPhotoType)
        val etDescription = view.findViewById<TextInputEditText>(R.id.etDescription)

        val selectedTypeId = rgPhotoType.checkedRadioButtonId
        if (selectedTypeId == -1) {
            Snackbar.make(view, "Please select photo type", Snackbar.LENGTH_SHORT).show()
            return
        }

        val selectedType = view.findViewById<RadioButton>(selectedTypeId).text.toString()
        val description = etDescription.text.toString()

        if (currentPhotoPath.isNotEmpty()) {
            onPhotoAdded(
                currentPhotoPath,
                selectedType.uppercase(),
                description.takeIf { it.isNotBlank() }
            )
            dismiss()
        } else {
            Snackbar.make(view, "No photo selected", Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "AddPhotoDialog"
    }
}