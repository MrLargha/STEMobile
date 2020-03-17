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

    public void deleteSubstitution(Substitution substitution) {
        STERoomDatabase.getDatabaseExecutor().execute(() -> steDao.deleteByUID(substitution.getID()));
        // Delete from server if it is synchronized
        if (substitution.getStatus().equals(Substitution.STATUS_SYNCHRONIZED))
            dataSource.deleteSubstitution(LoginRepository.getInstance(dataSource).getToken(), substitution);
    }

    public LiveData<List<Substitution>> getSubstitutions() {
        return steDao.getAllSubstitutions();
    }

    private LinkedList<Substitution> getSubstitutionsSync() {
        return new LinkedList<>(Arrays.asList(steDao.getAllSubstitutionsSync()));
    }

    public void deleteAllLocalSubstitutions(){
        STERoomDatabase.getDatabaseExecutor().execute(() -> steDao.deleteAll());
    }

    public LinkedList<Substitution> getUnSyncSubstitutions() {
        return new LinkedList<>(Arrays.asList(steDao.getUnSyncSubstitutions()));
    }

    public int downloadUpdate() throws SynchronizationException {
        Calendar reference = Calendar.getInstance();
        reference.set(Calendar.HOUR_OF_DAY, reference.getActualMinimum(Calendar.HOUR_OF_DAY));
        reference.set(Calendar.MINUTE, reference.getActualMinimum(Calendar.MINUTE));
        reference.set(Calendar.SECOND, reference.getActualMinimum(Calendar.SECOND));
        reference.set(Calendar.MILLISECOND, reference.getActualMinimum(Calendar.MILLISECOND));

        Result<SubstitutionsReply> serverSubstitutionsReply
                = getSubstitutionsFromServer((int) reference.getTime().getTime());
        LinkedList<Substitution> localSubstitutions = getSubstitutionsSync();
        int insertedCounter = 0;
        if (serverSubstitutionsReply instanceof Result.Success) {
            List<Substitution> serverSubstitutionsList = ((SubstitutionsReply) ((Result.Success)
                    serverSubstitutionsReply).getData()).getSubstitutions();
            for (Substitution substitution : localSubstitutions) {
                if (substitution.getStatus().equals(Substitution.STATUS_SYNCHRONIZED)) {
                    boolean hasOnServer = false;
                    for (Substitution serverSubstitution : serverSubstitutionsList) {
                        if (serverSubstitution.fullEquals(substitution)) {
                            hasOnServer = true;
                            break;
                        }
                    }
                    if (!hasOnServer) {
                        steDao.deleteByUID(substitution.getID());
                    }
                }
            }
            for (Substitution serverSubstitution : serverSubstitutionsList) {
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
                    insertedCounter++;
                }
            }
        } else {
            throw new SynchronizationException(((Result.Error) serverSubstitutionsReply).getErrorString());
        }
        return insertedCounter;
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

    Result<SubstitutionsReply> getSubstitutionsFromServer(int date) {
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
