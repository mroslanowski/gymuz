package com.example.gymuz

import android.app.Application
import com.example.gymuz.database.AppDatabase

class App : Application() {

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()

        db = AppDatabase.getDatabase(this)
    }
}
