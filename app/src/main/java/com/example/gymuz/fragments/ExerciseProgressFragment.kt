package com.example.gymuz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymuz.R
import com.example.gymuz.adapters.ExerciseProgressAdapter
import com.example.gymuz.database.AppDatabase
import com.example.gymuz.database.repository.ProgressRepository
import com.example.gymuz.dialogs.AddExerciseProgressDialog
import com.example.gymuz.login.UserPreferences
import com.example.gymuz.views.SimpleChartView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ExerciseProgressFragment : Fragment() {

    private lateinit var repository: ProgressRepository
    private lateinit var userPreferences: UserPreferences
    private var currentUserId: Int = -1

    private lateinit var spinnerExercises: Spinner
    private lateinit var btnAddExerciseProgress: Button
    private lateinit var exerciseChart: SimpleChartView
    private lateinit var rvExerciseProgress: RecyclerView
    private lateinit var exerciseAdapter: ExerciseProgressAdapter

    private var exerciseNames: List<String> = emptyList()
    private var selectedExercise: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exercise_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeComponents(view)
        setupRepository()
        setupRecyclerView()
        setupSpinner()
        setupClickListeners()
        loadExerciseData()
    }

    private fun initializeComponents(view: View) {
        spinnerExercises = view.findViewById(R.id.spinnerExercises)
        btnAddExerciseProgress = view.findViewById(R.id.btnAddExerciseProgress)
        exerciseChart = view.findViewById(R.id.exerciseChart)
        rvExerciseProgress = view.findViewById(R.id.rvExerciseProgress)
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
        exerciseAdapter = ExerciseProgressAdapter(
            mutableListOf(),
            onDeleteClick = { progress ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        repository.deleteExerciseProgress(progress)
                        exerciseAdapter.removeProgress(progress)
                        loadExerciseProgressForSelected()
                        Snackbar.make(requireView(), "Exercise progress deleted", Snackbar.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Snackbar.make(
                            requireView(),
                            "Error deleting progress: ${e.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )

        rvExerciseProgress.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = exerciseAdapter
        }
    }

    private fun setupSpinner() {
        spinnerExercises.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (exerciseNames.isNotEmpty() && position < exerciseNames.size) {
                    selectedExercise = exerciseNames[position]
                    loadExerciseProgressForSelected()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedExercise = null
            }
        }
    }

    private fun setupClickListeners() {
        btnAddExerciseProgress.setOnClickListener {
            showAddExerciseProgressDialog()
        }
    }

    fun showAddExerciseProgressDialog() {
        val dialog = AddExerciseProgressDialog { exerciseName, weight, sets, reps, notes ->
            addExerciseProgress(exerciseName, weight, sets, reps, notes)
        }
        dialog.show(parentFragmentManager, AddExerciseProgressDialog.TAG)
    }

    private fun addExerciseProgress(exerciseName: String, weight: Float, sets: Int, reps: Int, notes: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                repository.addExerciseProgress(currentUserId, exerciseName, weight, sets, reps, notes)
                loadExerciseData() // Reload to update spinner if new exercise
                Snackbar.make(requireView(), "Exercise progress added", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Error adding exercise progress: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadExerciseData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Load unique exercise names
                exerciseNames = repository.getUniqueExerciseNames(currentUserId)
                updateSpinner()

                // Load progress for currently selected exercise
                loadExerciseProgressForSelected()

            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Error loading exercise data: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            exerciseNames.ifEmpty { listOf("No exercises yet") }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerExercises.adapter = adapter

        if (exerciseNames.isNotEmpty()) {
            selectedExercise = exerciseNames[0]
        }
    }

    private fun loadExerciseProgressForSelected() {
        selectedExercise?.let { exerciseName ->
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // Load progress for selected exercise
                    val progressList = repository.getExerciseProgressByName(currentUserId, exerciseName)
                    exerciseAdapter.updateProgress(progressList)

                    // Update chart
                    updateChart(progressList, exerciseName)

                } catch (e: Exception) {
                    Snackbar.make(
                        requireView(),
                        "Error loading exercise progress: ${e.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun updateChart(progressList: List<com.example.gymuz.database.entity.ExerciseProgress>, exerciseName: String) {
        val chartPoints = progressList.map { progress ->
            SimpleChartView.ChartPoint(
                x = progress.timestamp.toFloat(),
                y = progress.weight,
                label = progress.date.substring(5) // Show MM-dd format
            )
        }

        exerciseChart.setData(
            chartPoints,
            "$exerciseName Progress",
            "Weight (kg)"
        )
    }
}