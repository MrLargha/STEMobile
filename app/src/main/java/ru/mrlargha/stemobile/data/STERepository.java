package ru.mrlargha.stemobile.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import ru.mrlargha.stemobile.data.model.SimpleServerReply;
import ru.mrlargha.stemobile.data.model.Substitution;
import ru.mrlargha.stemobile.data.model.SubstitutionFormHints;
import ru.mrlargha.stemobile.data.model.SubstitutionsReply;
import ru.mrlargha.stemobile.data.model.UsersReply;
import ru.mrlargha.stemobile.database.STEDao;
import ru.mrlargha.stemobile.database.STERoomDatabase;

public class STERepository {
    private static final String TAG = "stemobile";
    private static STERepository INSTANCE;
    private STEDao steDao;
    private STEDataSource dataSource;

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

    public boolean deleteSubstitution(Substitution substitution) {
        if (dataSource.deleteSubstitution(LoginRepository.getInstance(dataSource).getToken(), substitution)
                instanceof Result.Success) {
            STERoomDatabase.getDatabaseExecutor().execute(() -> steDao.deleteByUID(substitution.getID()));
            return true;
        }
        return false;
    }

    public LiveData<List<Substitution>> getSubstitutions() {
        return steDao.getAllSubstitutions();
    }

    public void downloadUpdate() throws SynchronizationException {
        Calendar reference = Calendar.getInstance();
        reference.set(Calendar.HOUR_OF_DAY, reference.getActualMinimum(Calendar.HOUR_OF_DAY));
        reference.set(Calendar.MINUTE, reference.getActualMinimum(Calendar.MINUTE));
        reference.set(Calendar.SECOND, reference.getActualMinimum(Calendar.SECOND));
        reference.set(Calendar.MILLISECOND, reference.getActualMinimum(Calendar.MILLISECOND));

        Result<SubstitutionsReply> serverSubstitutions
                = getSubstitutionsFromServer((int) reference.getTime().getTime());
        LinkedList<Substitution> localSubstitutions =
                new LinkedList<>(Arrays.asList(steDao.getAllSubstitutionsSync()));
        if (serverSubstitutions instanceof Result.Success) {
            for (Substitution serverSubstitution : ((SubstitutionsReply) ((Result.Success)
                    serverSubstitutions).getData()).getSubstitutions()) {
                boolean hasOnClient = false;
                for (Substitution localSubstitution : localSubstitutions) {
                    if (localSubstitution.fullEquals(serverSubstitution)) {
                        hasOnClient = true;
                        break;
                    }
                }
                if (!hasOnClient) {
                    serverSubstitution.setStatus(Substitution.STATUS_SYNCHRONIZED);
                    insertSubstitutionToDB(serverSubstitution);
                }
            }
        } else {
            throw new SynchronizationException(((Result.Error) serverSubstitutions).getErrorString());
        }
    }

    public Result<SubstitutionFormHints> getFormHints() {
        return dataSource.getFormHints();
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

    public Result<UsersReply> getUsers() {
        return dataSource.getUsers(LoginRepository.getInstance(dataSource).getToken());
    }

    public Result<SimpleServerReply> setUserPermission(int user_id, String permission) {
        return dataSource.setPermission(LoginRepository.getInstance(dataSource).getToken(),
                                        user_id, permission);
    }

    public static class SynchronizationException extends IOException {
        SynchronizationException(String message) {
            super(message);
        }
    }
}
