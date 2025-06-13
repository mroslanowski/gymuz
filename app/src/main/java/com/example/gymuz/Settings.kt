package com.example.gymuz

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import java.util.*

class Settings : Fragment() {
    private lateinit var darkModeSwitch: SwitchMaterial
    private lateinit var notificationSwitch: SwitchMaterial
    private lateinit var languageSwitch: SwitchMaterial
    private lateinit var tvNotificationTime: TextView

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
        val currentLang = sharedPrefs.getString("app_language", "en")

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

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("notifications_enabled", isChecked).apply()
            if (isChecked) {
                scheduleNotification(hour, minute)
            } else {
                cancelNotification()
            }
        }

        languageSwitch.setOnCheckedChangeListener { _, isChecked ->
            val langCode = if (isChecked) "pl" else "en"
            sharedPrefs.edit().putString("app_language", langCode).apply()
            setLocale(langCode)
            requireActivity().recreate()
        }

        tvNotificationTime.setOnClickListener {
            showTimePickerDialog(hour, minute)
        }

        return view
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
        if (notificationSwitch.isChecked) {
            scheduleNotification(hour, minute)
        }
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = requireContext().resources.configuration
        config.setLocale(locale)
        requireContext().createConfigurationContext(config)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
    }

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
            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
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