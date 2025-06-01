package com.example.gymuz.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymuz.R
import com.example.gymuz.adapters.WeightEntryAdapter
import com.example.gymuz.database.AppDatabase
import com.example.gymuz.database.repository.ProgressRepository
import com.example.gymuz.dialogs.AddWeightDialog
import com.example.gymuz.login.UserPreferences
import com.example.gymuz.views.SimpleChartView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class WeightProgressFragment : Fragment() {

    private lateinit var repository: ProgressRepository
    private lateinit var userPreferences: UserPreferences
    private var currentUserId: Int = -1

    private lateinit var tvCurrentWeight: TextView
    private lateinit var btnAddWeight: Button
    private lateinit var weightChart: SimpleChartView
    private lateinit var rvWeightEntries: RecyclerView
    private lateinit var weightAdapter: WeightEntryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weight_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeComponents(view)
        setupRepository()
        setupRecyclerView()
        setupClickListeners()
        loadWeightData()
    }

    private fun initializeComponents(view: View) {
        tvCurrentWeight = view.findViewById(R.id.tvCurrentWeight)
        btnAddWeight = view.findViewById(R.id.btnAddWeight)
        weightChart = view.findViewById(R.id.weightChart)
        rvWeightEntries = view.findViewById(R.id.rvWeightEntries)
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
        weightAdapter = WeightEntryAdapter(
            mutableListOf(),
            onDeleteClick = { weightEntry ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        repository.deleteWeightEntry(weightEntry)
                        weightAdapter.removeEntry(weightEntry)
                        loadWeightData() // Refresh data
                        Snackbar.make(requireView(), "Weight entry deleted", Snackbar.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Snackbar.make(
                            requireView(),
                            "Error deleting entry: ${e.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )

        rvWeightEntries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = weightAdapter
        }
    }

    private fun setupClickListeners() {
        btnAddWeight.setOnClickListener {
            showAddWeightDialog()
        }
    }

    fun showAddWeightDialog() {
        val dialog = AddWeightDialog { weight, notes ->
            addWeightEntry(weight, notes)
        }
        dialog.show(parentFragmentManager, AddWeightDialog.TAG)
    }

    private fun addWeightEntry(weight: Float, notes: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                repository.addWeightEntry(currentUserId, weight, notes)
                loadWeightData()
                Snackbar.make(requireView(), "Weight entry added", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Error adding weight entry: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadWeightData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Load current weight
                val latestWeight = repository.getLatestWeight(currentUserId)
                updateCurrentWeight(latestWeight?.weight)

                // Load recent entries for list
                val recentEntries = repository.getRecentWeightEntries(currentUserId)
                weightAdapter.updateEntries(recentEntries)

                // Load chart data (last 30 days)
                val startDate = repository.getDateDaysAgo(30)
                val chartEntries = repository.getWeightEntriesFromDate(currentUserId, startDate)
                updateChart(chartEntries)

            } catch (e: Exception) {
                Snackbar.make(
                    requireView(),
                    "Error loading weight data: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateCurrentWeight(weight: Float?) {
        val decimalFormat = DecimalFormat("#.#")
        tvCurrentWeight.text = if (weight != null) {
            "${decimalFormat.format(weight)} kg"
        } else {
            "-- kg"
        }
    }

    private fun updateChart(entries: List<com.example.gymuz.database.entity.WeightEntry>) {
        val chartPoints = entries.map { entry ->
            SimpleChartView.ChartPoint(
                x = entry.timestamp.toFloat(),
                y = entry.weight,
                label = entry.date.substring(5) // Show MM-dd format
            )
        }

        weightChart.setData(
            chartPoints,
            "Weight Progress (Last 30 days)",
            "Weight (kg)"
        )
    }
}