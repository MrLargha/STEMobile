package ru.mrlargha.stemobile.ui.main;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ru.mrlargha.stemobile.data.Result;
import ru.mrlargha.stemobile.data.STERepository;
import ru.mrlargha.stemobile.data.model.SimpleServerReply;
import ru.mrlargha.stemobile.data.model.Substitution;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "stemobile";

    private STERepository steRepository;

    private LiveData<List<Substitution>> substitutionsList;
    private MutableLiveData<Integer> syncProgress = new MutableLiveData<>(-1);
    private MutableLiveData<String> undoString = new MutableLiveData<>();
    private MutableLiveData<String> errorString = new MutableLiveData<>();
    private MutableLiveData<String> statusString = new MutableLiveData<>();
    private ArrayList<Substitution> savedSubstitutions = new ArrayList<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        steRepository = STERepository.getRepository(application.getApplicationContext());
        substitutionsList = steRepository.getSubstitutions();
        syncSubstitutions(false);
    }

    void deleteSubstitutions(ArrayList<Long> ids) {
        savedSubstitutions.clear();
        boolean canUndo = false;
        int serverDel = 0;
        if (substitutionsList.getValue() != null) {
            for (Substitution substitution : substitutionsList.getValue()) {
                if (ids.contains((long) substitution.getID())) {
                    if (substitution.getStatus().equals(Substitution.STATUS_NOT_SYNCHRONIZED)) {
                        savedSubstitutions.add(substitution);
                        canUndo = true;
                    } else {
                        serverDel++;
                    }
                    new DeleteTask().execute(substitution);
                }
            }
        }
        if (canUndo) {
            if (serverDel > 0) {
                // No sense to undo it
                statusString.setValue(String.format("Из локального хранилища удалено %s замещений." +
                                "С сервера удалено %s замещений",
                        savedSubstitutions.size(), serverDel));
            } else {
                undoString.setValue(String.format("Из локального хранилища удалено %s замещений.",
                        savedSubstitutions.size()));
            }
        } else {
            statusString.setValue(String.format("С сервера удалено %s замещений.", serverDel));
        }
    }

    MutableLiveData<String> getUndoString() {
        return undoString;
    }

    void undoLocalDeletion() {
        for (Substitution substitution : savedSubstitutions) {
            steRepository.insertSubstitutionToDB(substitution);
        }
        clearDeletionCache();
    }

    void clearDeletionCache() {
        savedSubstitutions.clear();
        undoString.setValue("");
    }

    LiveData<List<Substitution>> getSubstitutionsList() {
        return substitutionsList;
    }

    MutableLiveData<String> getErrorString() {
        return errorString;
    }

    void syncSubstitutions(boolean upload) {
        new FetchTask().execute(upload);
    }

    MutableLiveData<Integer> getSyncProgress() {
        return syncProgress;
    }

    MutableLiveData<String> getStatusString() {
        return statusString;
    }

    private class DeleteTask extends AsyncTask<Substitution, Void, Void> {
        @Override
        protected Void doInBackground(Substitution... substitutions) {
            steRepository.deleteSubstitution(substitutions[0]);
            return null;
        }
    }

    void forceSync(){
        steRepository.deleteAllLocalSubstitutions();
        new FetchTask().execute(false);
    }

    // The firs parameter used as flag: true - upload local after sync, false - no upload
    private class FetchTask extends AsyncTask<Boolean, Integer, Boolean> {
        @Override
        protected final Boolean doInBackground(Boolean... booleans) {
            try {
                publishProgress(0);
                int downloaded = steRepository.downloadUpdate();
                if (downloaded > 0) {
                    statusString.postValue("Загружено " + downloaded + " замещений");
                }
            } catch (STERepository.SynchronizationException e) {
                errorString.postValue(e.getMessage());
                publishProgress(-1);
                return false;
            }
            if (!booleans[0]) {
                publishProgress(-1);
            }
            return booleans[0];
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            syncProgress.setValue(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                new UploadTask().execute();
            }
        }
    }

    private class UploadTask extends AsyncTask<Void, Integer, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            int progress = 0;
            publishProgress(progress);
            LinkedList<Substitution> substitutions = steRepository.getUnSyncSubstitutions();
            if (substitutions.isEmpty()) {
                publishProgress(-1);
                return null;
            }
            for (Substitution substitution : substitutions) {
                Result<SimpleServerReply> result = steRepository.sendSubstitution(substitution);
                progress += 100 / substitutions.size();
                publishProgress(progress);
                if (!(result instanceof Result.Success)) {
                    errorString.postValue(((Result.Error) result).getErrorString());
                } else {
                    Result.Success<SimpleServerReply> success =
                            (Result.Success<SimpleServerReply>) result;
                    if (success.getData().getStatus().equals("error")) {
                        steRepository.setSubstitutionStatus(substitution.getID(),
                                Substitution.STATUS_ERROR);
                    } else {
                        steRepository.setSubstitutionStatus(substitution.getID(),
                                Substitution.STATUS_SYNCHRONIZED);
                    }
                }
            }
            publishProgress(-1);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            syncProgress.setValue(values[0]);
        }
    }
}
