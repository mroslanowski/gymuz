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
    private lateinit var tvNotificationTime: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        darkModeSwitch = view.findViewById(R.id.switch_dark_mode)
        notificationSwitch = view.findViewById(R.id.switch_workout_reminders)
        tvNotificationTime = view.findViewById(R.id.tv_notification_time)

        val sharedPrefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", false)
        val hour = sharedPrefs.getInt("notification_hour", 8)
        val minute = sharedPrefs.getInt("notification_minute", 0)

        darkModeSwitch.isChecked = isDarkMode
        notificationSwitch.isChecked = notificationsEnabled
        tvNotificationTime.text = "Notification Time: %02d:%02d".format(hour, minute)

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

        tvNotificationTime.setOnClickListener {
            val picker = TimePickerDialog(requireContext(), { _, h, m ->
                tvNotificationTime.text = "Notification Time: %02d:%02d".format(h, m)
                sharedPrefs.edit().putInt("notification_hour", h).putInt("notification_minute", m).apply()
                if (notificationSwitch.isChecked) {
                    scheduleNotification(h, m)
                }
            }, hour, minute, true)
            picker.show()
        }

        return view
    }

    private fun scheduleNotification(hour: Int, minute: Int) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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
            requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}