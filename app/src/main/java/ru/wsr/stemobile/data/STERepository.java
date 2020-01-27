package ru.wsr.stemobile.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import ru.wsr.stemobile.data.model.Substitution;
import ru.wsr.stemobile.database.STEDao;
import ru.wsr.stemobile.database.STERoomDatabase;

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
        STERoomDatabase.getDatabaseExecutor().execute(() -> steDao.insertSubstitution(substitution));
    }

    public void deleteSubstitution(long uid) {
        STERoomDatabase.getDatabaseExecutor().execute(() -> steDao.deleteByUID(uid));

    }

    public LiveData<List<Substitution>> getAllSubstitutions() {
        return steDao.getAllSubstitutions();
    }
}
