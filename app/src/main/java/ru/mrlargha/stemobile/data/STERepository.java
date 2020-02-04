package ru.mrlargha.stemobile.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import ru.mrlargha.stemobile.data.model.SimpleServerReply;
import ru.mrlargha.stemobile.data.model.Substitution;
import ru.mrlargha.stemobile.data.model.SubstitutionsReply;
import ru.mrlargha.stemobile.database.STEDao;
import ru.mrlargha.stemobile.database.STERoomDatabase;

public class STERepository {
    private static final String TAG = "stemobile";
    private static STERepository INSTANCE;
    private STEDao steDao;
    private STEDataSource dataSource;

    private boolean isSendingRequired = false;

    private STERepository(final Context context) {
        STERoomDatabase steRoomDatabase = STERoomDatabase.getDatabase(context);
        steDao = steRoomDatabase.substitutionDao();
        dataSource = new STEDataSource();
        Log.d(TAG, "STERepository: created");
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

    public void setSubstitutionStatus(int id, String status) {
        steDao.setStatus(id, status);
    }

    public Result<SimpleServerReply> sendSubstitution(Substitution substitution) {
        return dataSource.sendSubstitution(substitution, LoginRepository.getInstance(dataSource).getToken());
    }

    public Result<SubstitutionsReply> getSubstitutionsFromServer(int date) {
        return dataSource.getSubstitutions(LoginRepository.getInstance(dataSource).getToken(), date);
    }
}
