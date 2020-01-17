package ru.wsr.stemobile.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.wsr.stemobile.model.Substitution;

@Database(entities = {Substitution.class}, version = 1, exportSchema = false)
public abstract class STERoomDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile STERoomDatabase INSTANCE;

    public static STERoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    STERoomDatabase.class,
                    "ste_database").build();
        }
        return INSTANCE;
    }

    public abstract STEDao substitutionDao();

}
