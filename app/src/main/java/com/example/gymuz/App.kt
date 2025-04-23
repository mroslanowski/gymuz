package com.example.gymuz

import android.app.Application
import com.example.gymuz.database.AppDatabase
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer // Potrzebny import

class App : Application() {

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()

        db = AppDatabase.getDatabase(this)

        // Wklej tutaj swój klucz API z MapTiler Cloud
        val mapTilerApiKey = "3ZaydqZeHLuyVKHfV6Yj"

        // Inicjalizuj z kluczem i serwerem MapTiler
        MapLibre.getInstance(this, mapTilerApiKey, WellKnownTileServer.MapTiler)
    }
}