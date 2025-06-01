package com.example.gymuz

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gymuz.database.AppDatabase
import com.example.gymuz.database.repository.WorkoutRepository
import com.example.gymuz.login.UserPreferences
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PdfGenerator : Fragment() {
    private lateinit var repository: WorkoutRepository
    private lateinit var userPreferences: UserPreferences
    private lateinit var btnGeneratePdf: Button
    private val STORAGE_PERMISSION_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pdf_generator1, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        repository = WorkoutRepository(
            db.workoutPlanDao(),
            db.workoutDayDao(),
            db.exerciseDao()
        )
        userPreferences = UserPreferences(requireContext())

        btnGeneratePdf = view.findViewById(R.id.btnGeneratePdf)
        btnGeneratePdf.setOnClickListener {
            if (checkPermission()) {
                generatePdf()
            } else {
                requestPermission()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generatePdf() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = userPreferences.getUserId()
                val plan = repository.getWorkoutPlanForUser(userId)
                if (plan != null) {
                    val fileName = "WorkoutPlan_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.pdf"
                    val filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName).absolutePath
                    } else {
                        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$fileName"
                    }

                    val file = File(filePath)
                    file.parentFile?.mkdirs() // Create directories if they don't exist

                    FileOutputStream(file).use { fos ->
                        val pdfWriter = PdfWriter(fos)
                        val pdfDocument = PdfDocument(pdfWriter)
                        val document = Document(pdfDocument)

                        document.add(Paragraph("Plan treningowy"))

                        val workoutDays = repository.getWorkoutDays(plan.id)
                        for (day in workoutDays) {
                            document.add(Paragraph("\n${day.dayName}${day.customDayName?.let { " - $it" } ?: ""}"))

                            val exercises = repository.getExercisesForDay(day.id)
                            for (exercise in exercises) {
                                document.add(Paragraph("• ${exercise.name}: ${exercise.sets} serie × ${exercise.reps} powtórzeń @ ${exercise.weight}kg"))
                            }
                        }

                        document.close()
                        pdfDocument.close()
                        Snackbar.make(requireView(), "PDF wygenerowany: $fileName", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    Snackbar.make(requireView(), "Nie znaleziono planu treningowego", Snackbar.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Snackbar.make(requireView(), "Błąd podczas generowania PDF: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true
        } else {
            val write = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generatePdf()
            } else {
                Snackbar.make(requireView(), "Wymagane uprawnienia do zapisu pliku", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}