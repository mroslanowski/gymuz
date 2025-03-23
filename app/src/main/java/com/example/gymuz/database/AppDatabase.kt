package com.example.gymuz.database
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Users::class], version = 1, exportSchema = false) // Zmień wersję przy zmianach schematu
abstract class AppDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "users_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}