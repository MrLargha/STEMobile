package ru.wsr.stemobile.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import ru.wsr.stemobile.database.STEDao;
import ru.wsr.stemobile.database.STERoomDatabase;
import ru.wsr.stemobile.model.Substitution;

public class STERepository {
    private static STERepository INSTANCE;
    private STERoomDatabase steRoomDatabase;
    private STEDao steDao;

    private STERepository(final Context context) {
        steRoomDatabase = STERoomDatabase.getDatabase(context);
        steDao = steRoomDatabase.substitutionDao();
    }

    public static STERepository getRepository(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = new STERepository(context);
        }
        return INSTANCE;
    }

    public void insertSubstitutionToDB(Substitution substitution) {
        steDao.insertSubstitution(substitution);
    }

    public LiveData<List<Substitution>> getAllSubstitutions() {
        return steDao.getAllSubstitutions();
    }
}
