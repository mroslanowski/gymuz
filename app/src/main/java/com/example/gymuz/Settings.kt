package com.example.gymuz

import com.example.gymuz.LocaleHelper
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*

class Settings : Fragment() {
    private lateinit var darkModeSwitch: SwitchMaterial
    private lateinit var notificationSwitch: SwitchMaterial
    private lateinit var languageSwitch: SwitchMaterial
    private lateinit var tvNotificationTime: TextView

    // --- NEW: Register the permissions callback ---
    // This handles the user's response to the system permissions dialog.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            val sharedPrefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
            if (isGranted) {
                // Permission is granted. Continue and schedule the notification.
                Toast.makeText(requireContext(), "Notifications enabled!", Toast.LENGTH_SHORT).show()
                val hour = sharedPrefs.getInt("notification_hour", 8)
                val minute = sharedPrefs.getInt("notification_minute", 0)
                scheduleNotification(hour, minute)
            } else {
                // Permission was denied. Inform the user and turn the switch back off.
                Toast.makeText(requireContext(), "Notifications permission denied.", Toast.LENGTH_LONG).show()
                notificationSwitch.isChecked = false
                sharedPrefs.edit().putBoolean("notifications_enabled", false).apply()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        darkModeSwitch = view.findViewById(R.id.switch_dark_mode)
        notificationSwitch = view.findViewById(R.id.switch_workout_reminders)
        languageSwitch = view.findViewById(R.id.switch_language)
        tvNotificationTime = view.findViewById(R.id.tv_notification_time)

        val sharedPrefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", false)
        val hour = sharedPrefs.getInt("notification_hour", 8)
        val minute = sharedPrefs.getInt("notification_minute", 0)

        // Use LocaleHelper for language, as per our BaseActivity setup
        val currentLang = LocaleHelper.getLanguage(requireContext())

        darkModeSwitch.isChecked = isDarkMode
        notificationSwitch.isChecked = notificationsEnabled
        languageSwitch.isChecked = currentLang == "pl"
        tvNotificationTime.text = getString(R.string.notification_time_format, hour, minute)

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // --- UPDATED: Notification Switch Listener ---
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("notifications_enabled", isChecked).apply()
            if (isChecked) {
                // Instead of scheduling directly, ask for permission first.
                askForNotificationPermission()
            } else {
                // If turning off, just cancel it.
                cancelNotification()
                Toast.makeText(requireContext(), "Notifications disabled.", Toast.LENGTH_SHORT).show()
            }
        }

        languageSwitch.setOnCheckedChangeListener { _, isChecked ->
            val langCode = if (isChecked) "pl" else "en"
            // Use the LocaleHelper to set the language and recreate
            LocaleHelper.setLocale(requireContext(), langCode)
            requireActivity().recreate()
        }

        tvNotificationTime.setOnClickListener {
            // Get the latest hour/minute values when clicked
            val currentHour = sharedPrefs.getInt("notification_hour", 8)
            val currentMinute = sharedPrefs.getInt("notification_minute", 0)
            showTimePickerDialog(currentHour, currentMinute)
        }

        return view
    }

    // --- NEW: Function to handle the permission request flow ---
    private fun askForNotificationPermission() {
        // This is only necessary for API level 33+ (Android 13)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted. You can schedule the notification.
                val sharedPrefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
                val hour = sharedPrefs.getInt("notification_hour", 8)
                val minute = sharedPrefs.getInt("notification_minute", 0)
                scheduleNotification(hour, minute)
            } else {
                // Directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // For older Android versions, no runtime permission is needed.
            val sharedPrefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
            val hour = sharedPrefs.getInt("notification_hour", 8)
            val minute = sharedPrefs.getInt("notification_minute", 0)
            scheduleNotification(hour, minute)
        }
    }

    private fun showTimePickerDialog(currentHour: Int, currentMinute: Int) {
        TimePickerDialog(
            requireContext(),
            { _, h, m ->
                updateNotificationTime(h, m)
            },
            currentHour,
            currentMinute,
            true
        ).show()
    }

    private fun updateNotificationTime(hour: Int, minute: Int) {
        tvNotificationTime.text = getString(R.string.notification_time_format, hour, minute)
        requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
            .edit()
            .putInt("notification_hour", hour)
            .putInt("notification_minute", minute)
            .apply()
        // If notifications are already enabled, reschedule with the new time
        if (notificationSwitch.isChecked) {
            scheduleNotification(hour, minute)
        }
    }

    // This old function is no longer needed because BaseActivity and LocaleHelper handle it.
    // private fun setLocale(language: String) { ... }

    private fun scheduleNotification(hour: Int, minute: Int) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If the time is in the past for today, schedule it for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Use setInexactRepeating for better battery performance, or setRepeating for precision
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(requireContext(), "Reminder set for ${String.format("%02d:%02d", hour, minute)}", Toast.LENGTH_SHORT).show()
    }

    private fun cancelNotification() {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}