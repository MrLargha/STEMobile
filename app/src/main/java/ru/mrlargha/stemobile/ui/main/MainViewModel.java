package ru.mrlargha.stemobile.ui.main;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import ru.mrlargha.stemobile.data.STERepository;
import ru.mrlargha.stemobile.data.model.Substitution;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "stemobile";

    private STERepository steRepository;

    private LiveData<List<Substitution>> substitutionsList;
    private MutableLiveData<Integer> syncProgress = new MutableLiveData<>(-1);
    private MutableLiveData<String> undoString = new MutableLiveData<>();
    private MutableLiveData<String> errorString = new MutableLiveData<>("");
    private ArrayList<Substitution> savedSubstitutions = new ArrayList<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
        steRepository = STERepository.getRepository(application.getApplicationContext());
        substitutionsList = steRepository.getSubstitutions();
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
                undoString.setValue(String.format("Из локального хранилища удалено %s замещений." +
                                                          "С сервера удалено %s замещений",
                                                  savedSubstitutions.size(), serverDel));
            } else {
                undoString.setValue(String.format("Из локального хранилища удалено %s замещений.",
                                                  savedSubstitutions.size()));
            }
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

    void syncSubstitutions() {
        new FetchTask().execute();
    }

    MutableLiveData<Integer> getSyncProgress() {
        return syncProgress;
    }

    private class DeleteTask extends AsyncTask<Substitution, Void, Void> {

        @Override
        protected Void doInBackground(Substitution... substitutions) {
            steRepository.deleteSubstitution(substitutions[0]);
            return null;
        }
    }

    private class FetchTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected final Void doInBackground(Void... voids) {
            try {
                steRepository.downloadUpdate();
            } catch (STERepository.SynchronizationException e) {
                errorString.postValue(e.getMessage());
            }
            return null;
//            LinkedList<SimpleServerReply> replies = new LinkedList<>();
//            for (Substitution substitution : pendingSubstitutions) {
//                Result result = steRepository.sendSubstitution(substitution);
//                if (result instanceof Result.Success) {
//                    SimpleServerReply reply = ((Result.Success<SimpleServerReply>) result).getData();
//                    replies.add(reply);
//                    if (reply.getStatus().equals("ok")) {
//                        steRepository.setSubstitutionStatus(substitution.getID(),
//                                Substitution.STATUS_SYNCHRONIZED);
//                    } else {
//                        steRepository.setSubstitutionStatus(substitution.getID(),
//                                Substitution.STATUS_ERROR);
//                    }
//                } else {
//                    errorString.postValue("Ошибка синхронизации: " + ((Result.Error) result).getErrorString());
//                }
//                progress += 99 / pendingSubstitutions.size();
//                publishProgress(progress);
//            }
//            return replies;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            syncProgress.setValue(values[0]);
            Log.d(TAG, "onFetchProgressUpdate: Progress " + values[0]);
        }
    }
}
