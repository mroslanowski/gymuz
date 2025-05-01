package com.example.gymuz

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymuz.adapters.ExerciseAdapter
import com.example.gymuz.database.AppDatabase
import com.example.gymuz.database.entity.Exercise
import com.example.gymuz.database.dao.WorkoutDay
import com.example.gymuz.database.repository.WorkoutRepository
import com.example.gymuz.login.UserPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class Create_Plan : Fragment() {

    private lateinit var repository: WorkoutRepository
    private lateinit var userPreferences: UserPreferences
    private var currentUserId: Int = -1
    private var currentPlanId: Int = -1

    // UI components
    private lateinit var btnPreviousDay: ImageButton
    private lateinit var btnNextDay: ImageButton
    private lateinit var tvDayName: TextView
    private lateinit var etDayName: EditText
    private lateinit var rvExercises: RecyclerView
    private lateinit var emptyStateView: LinearLayout
    private lateinit var btnAddFirstExercise: Button
    private lateinit var fabAddExercise: FloatingActionButton

    // Dane do widoku
    private var workoutDays: List<WorkoutDay> = emptyList()
    private var currentDayIndex: Int = 0
    private lateinit var exerciseAdapter: ExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicjalizacja repozytoriów i preferencji
        val db = AppDatabase.getDatabase(requireContext())
        repository = WorkoutRepository(
            db.workoutPlanDao(),
            db.workoutDayDao(),
            db.exerciseDao()
        )
        userPreferences = UserPreferences(requireContext())
        currentUserId = userPreferences.getUserId()

        if (currentUserId == -1) {
            Snackbar.make(view, "Nie jesteś zalogowany", Snackbar.LENGTH_LONG).show()
            return
        }

        // Inicjalizacja widoków
        initializeViews(view)

        // Inicjalizacja planu treningowego
        initializeWorkoutPlan()
    }

    private fun initializeViews(view: View) {
        btnPreviousDay = view.findViewById(R.id.btnPreviousDay)
        btnNextDay = view.findViewById(R.id.btnNextDay)
        tvDayName = view.findViewById(R.id.tvDayName)
        etDayName = view.findViewById(R.id.etDayName)
        rvExercises = view.findViewById(R.id.rvExercises)
        emptyStateView = view.findViewById(R.id.emptyStateView)
        btnAddFirstExercise = view.findViewById(R.id.btnAddFirstExercise)
        fabAddExercise = view.findViewById(R.id.fabAddExercise)

        // Skonfiguruj RecyclerView
        rvExercises.layoutManager = LinearLayoutManager(requireContext())
        exerciseAdapter = ExerciseAdapter(
            mutableListOf(),
            { exercise -> updateExercise(exercise) },
            { exercise -> deleteExercise(exercise) }
        )
        rvExercises.adapter = exerciseAdapter

        // Skonfiguruj przyciski nawigacji dni
        btnPreviousDay.setOnClickListener { navigateToPreviousDay() }
        btnNextDay.setOnClickListener { navigateToNextDay() }

        // Skonfiguruj przyciski dodawania ćwiczeń
        fabAddExercise.setOnClickListener { showAddExerciseBottomSheet() }
        btnAddFirstExercise.setOnClickListener { showAddExerciseBottomSheet() }

        // Obsługa zmiany niestandardowej nazwy dnia
        etDayName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateCustomDayName(s?.toString())
            }
        })
    }

    private fun initializeWorkoutPlan() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Pobierz lub stwórz plan treningowy dla użytkownika
                val plan = repository.createWorkoutPlanForUser(currentUserId)
                currentPlanId = plan.id

                // Pobierz dni tygodnia
                workoutDays = repository.getWorkoutDays(currentPlanId)

                // Początkowo załaduj poniedziałek (index 1)
                currentDayIndex = 1 // Poniedziałek
                loadDayData()

            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Błąd podczas ładowania planu: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadDayData() {
        if (workoutDays.isEmpty()) return

        val currentDay = workoutDays[currentDayIndex]

        // Ustawienie nazwy dnia
        tvDayName.text = currentDay.dayName
        etDayName.setText(currentDay.customDayName ?: "")

        // Ładowanie ćwiczeń dla danego dnia
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val exercises = repository.getExercisesForDay(currentDay.id)

                // Aktualizuj adapter
                exerciseAdapter.updateExercises(exercises)

                // Pokaż widok pustego stanu lub listę ćwiczeń
                if (exercises.isEmpty()) {
                    rvExercises.visibility = View.GONE
                    emptyStateView.visibility = View.VISIBLE
                } else {
                    rvExercises.visibility = View.VISIBLE
                    emptyStateView.visibility = View.GONE
                }

            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Błąd podczas ładowania ćwiczeń: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun navigateToPreviousDay() {
        if (workoutDays.isEmpty()) return

        // Zmiana indeksu dnia (od 0 do 6)
        currentDayIndex = if (currentDayIndex > 0) currentDayIndex - 1 else 6
        loadDayData()
    }

    private fun navigateToNextDay() {
        if (workoutDays.isEmpty()) return

        // Zmiana indeksu dnia (od 0 do 6)
        currentDayIndex = (currentDayIndex + 1) % 7
        loadDayData()
    }

    private fun showAddExerciseBottomSheet() {
        if (workoutDays.isEmpty()) return

        val currentDay = workoutDays[currentDayIndex]

        // Tworzenie i wyświetlanie BottomSheetDialogFragment
        val bottomSheet = AddExerciseBottomSheet(
            currentDay.id,
            onExerciseAdded = { name, sets, reps, weight ->
                addExercise(currentDay.id, name, sets, reps, weight)
            }
        )
        bottomSheet.show(parentFragmentManager, AddExerciseBottomSheet.TAG)
    }

    private fun addExercise(dayId: Int, name: String, sets: Int, reps: Int, weight: Float) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val exercise = repository.addExercise(dayId, name, sets, reps, weight)

                // Dodaj ćwiczenie do adaptera
                exerciseAdapter.addExercise(exercise)

                // Aktualizuj widoczność widoków
                rvExercises.visibility = View.VISIBLE
                emptyStateView.visibility = View.GONE

                Snackbar.make(
                    requireView(),
                    "Dodano ćwiczenie: $name",
                    Snackbar.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Błąd podczas dodawania ćwiczenia: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateExercise(exercise: Exercise) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                repository.updateExercise(exercise)
            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Błąd podczas aktualizacji ćwiczenia: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun deleteExercise(exercise: Exercise) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                repository.deleteExercise(exercise)

                // Sprawdź, czy lista jest pusta po usunięciu
                val currentExercises = repository.getExercisesForDay(workoutDays[currentDayIndex].id)
                if (currentExercises.isEmpty()) {
                    rvExercises.visibility = View.GONE
                    emptyStateView.visibility = View.VISIBLE
                }

                Snackbar.make(
                    requireView(),
                    "Usunięto ćwiczenie: ${exercise.name}",
                    Snackbar.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Błąd podczas usuwania ćwiczenia: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateCustomDayName(customName: String?) {
        if (workoutDays.isEmpty()) return

        val currentDay = workoutDays[currentDayIndex]

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Zapisujemy pustą wartość jako null (dla czytelności w bazie)
                val nameToSave = if (customName.isNullOrBlank()) null else customName

                repository.updateWorkoutDayCustomName(currentDay.id, nameToSave)

                // Aktualizuj lokalną listę
                val updatedDay = currentDay.copy(customDayName = nameToSave)
                val mutableDays = workoutDays.toMutableList()
                mutableDays[currentDayIndex] = updatedDay
                workoutDays = mutableDays

            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Błąd podczas aktualizacji nazwy dnia: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
}