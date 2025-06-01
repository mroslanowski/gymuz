package com.example.gymuz.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gymuz.fragments.ExerciseProgressFragment
import com.example.gymuz.fragments.PhotoProgressFragment
import com.example.gymuz.fragments.WeightProgressFragment

class ProgressPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WeightProgressFragment()
            1 -> ExerciseProgressFragment()
            2 -> PhotoProgressFragment()
            else -> WeightProgressFragment()
        }
    }
}