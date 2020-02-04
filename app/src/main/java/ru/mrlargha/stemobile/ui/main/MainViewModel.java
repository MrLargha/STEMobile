package ru.mrlargha.stemobile.ui.main;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

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

    private ArrayList<Substitution> savedSubstitutions = new ArrayList<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "MainViewModel: created");
        steRepository = STERepository.getRepository(application.getApplicationContext());
        substitutionsList = steRepository.getAllSubstitutions();
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
                    steRepository.deleteSubstitution(substitution.getID());
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

    public void sendSubstitutions() {
        LinkedList<Substitution> pendingSubstitutions = new LinkedList<>();
        if (substitutionsList.getValue() != null) {
            for (Substitution substitution : substitutionsList.getValue()) {
                if (substitution.getStatus().equals(Substitution.STATUS_NOT_SYNCHRONIZED)) {
                    pendingSubstitutions.add(substitution);
                }
            }
        }
        new SendTask().execute(pendingSubstitutions);
    }

    private class SendTask extends AsyncTask<LinkedList<Substitution>, Integer, List<SimpleServerReply>> {

        @Override
        protected List<SimpleServerReply> doInBackground(LinkedList<Substitution>... linkedLists) {
            int progress = 0;
            LinkedList<SimpleServerReply> replies = new LinkedList<>();
            for (Substitution substitution : linkedLists[0]) {
                Result result = steRepository.sendSubstitution(substitution);
                if (result instanceof Result.Success) {
                    replies.add(((Result.Success<SimpleServerReply>) result).getData());
                }
                progress += 100 / linkedLists[0].size();
                publishProgress(progress);
            }
            return replies;
        }

        @Override
        protected void onPostExecute(List<SimpleServerReply> simpleServerReplies) {
            super.onPostExecute(simpleServerReplies);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "onProgressUpdate: Progress " + values[0]);
        }
    }
}
