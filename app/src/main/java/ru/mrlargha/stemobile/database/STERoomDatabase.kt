package ru.mrlargha.stemobile.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.mrlargha.stemobile.data.model.Substitution
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Substitution::class], version = 6, exportSchema = false)
abstract class STERoomDatabase : RoomDatabase() {

    abstract fun substitutionDao(): STEDao?

    companion object {
        private const val NUMBER_OF_THREADS = 4

        @JvmStatic
        val databaseExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        @Volatile
        private var INSTANCE: STERoomDatabase? = null

        fun getDatabase(context: Context): STERoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                        STERoomDatabase::class.java,
                        "ste_database").fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
