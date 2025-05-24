package com.example.gymuz

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class Settings : Fragment() {
    private lateinit var darkModeSwitch: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        darkModeSwitch = view.findViewById(R.id.switch_dark_mode)

        // Wczytaj zapisane preferencje
        val sharedPrefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)

        // Ustaw początkowy stan przełącznika
        darkModeSwitch.isChecked = isDarkMode

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Zapisz preferencję
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()

            // Zastosuj tryb
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        return view
    }
}