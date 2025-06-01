package com.example.gymuz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.gymuz.adapters.ProgressPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Progress : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnAddProgress: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)
        btnAddProgress = view.findViewById(R.id.btnAddProgress)

        setupTabs()
        setupAddButton()
    }

    private fun setupTabs() {
        val adapter = ProgressPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Weight"
                1 -> "Exercises"
                2 -> "Photos"
                else -> "Tab"
            }
        }.attach()
    }

    private fun setupAddButton() {
        btnAddProgress.setOnClickListener {
            // The add functionality will be handled by individual fragments
            // based on current tab position
            when (viewPager.currentItem) {
                0 -> {
                    // Trigger weight addition in WeightProgressFragment
                    val fragment = childFragmentManager.fragments.find {
                        it is com.example.gymuz.fragments.WeightProgressFragment
                    } as? com.example.gymuz.fragments.WeightProgressFragment
                    fragment?.showAddWeightDialog()
                }
                1 -> {
                    // Trigger exercise progress addition
                    val fragment = childFragmentManager.fragments.find {
                        it is com.example.gymuz.fragments.ExerciseProgressFragment
                    } as? com.example.gymuz.fragments.ExerciseProgressFragment
                    fragment?.showAddExerciseProgressDialog()
                }
                2 -> {
                    // Trigger photo addition
                    val fragment = childFragmentManager.fragments.find {
                        it is com.example.gymuz.fragments.PhotoProgressFragment
                    } as? com.example.gymuz.fragments.PhotoProgressFragment
                    fragment?.showAddPhotoDialog()
                }
            }
        }
    }
}