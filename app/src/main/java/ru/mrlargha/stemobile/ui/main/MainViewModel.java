package ru.mrlargha.stemobile.ui.main;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import ru.mrlargha.stemobile.data.Result;
import ru.mrlargha.stemobile.data.STERepository;
import ru.mrlargha.stemobile.data.model.SimpleServerReply;
import ru.mrlargha.stemobile.data.model.Substitution;
import ru.mrlargha.stemobile.data.model.SubstitutionsReply;

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

    void syncSubstitutions() {

        new SyncTask().execute(new LinkedList<>(substitutionsList.getValue()));
    }

    MutableLiveData<Integer> getSyncProgress() {
        return syncProgress;
    }

    private class SyncTask extends AsyncTask<LinkedList<Substitution>, Integer, List<SimpleServerReply>> {

        @SafeVarargs
        @Override
        protected final List<SimpleServerReply> doInBackground(LinkedList<Substitution>... linkedLists) {
            Calendar reference = Calendar.getInstance();
            reference.set(Calendar.HOUR_OF_DAY, reference.getActualMinimum(Calendar.HOUR_OF_DAY));
            reference.set(Calendar.MINUTE, reference.getActualMinimum(Calendar.MINUTE));
            reference.set(Calendar.SECOND, reference.getActualMinimum(Calendar.SECOND));
            reference.set(Calendar.MILLISECOND, reference.getActualMinimum(Calendar.MILLISECOND));
            Result<SubstitutionsReply> fromServer = steRepository.getSubstitutionsFromServer((int)
                    reference.getTime().getTime());

            // Find conflicts with the remote
            if (fromServer instanceof Result.Success) {
                for (Substitution serverSubstitution : ((SubstitutionsReply) ((Result.Success)
                        fromServer).getData()).getSubstitutions()) {
                    boolean isOk = true, hasOnClient = false;
                    int conflictedID = -1;
                    for (Substitution localSubstitution : linkedLists[0]) {
                        if (localSubstitution.fullEquals(serverSubstitution)) {
                            hasOnClient = true;
                            break;
                        } else if (localSubstitution.equals(serverSubstitution)) {
                            isOk = false;
                            conflictedID = localSubstitution.getID();
                            break;
                        }
                    }
                    if (!isOk) {
                        steRepository.setSubstitutionStatus(conflictedID, Substitution.STATUS_ERROR);
                    }
                    if (!hasOnClient) {
                        serverSubstitution.setStatus(Substitution.STATUS_SYNCHRONIZED);
                        steRepository.insertSubstitutionToDB(serverSubstitution);
                    }
                }
            }

            LinkedList<Substitution> pendingSubstitutions = new LinkedList<>();
            if (substitutionsList.getValue() != null) {
                for (Substitution substitution : linkedLists[0]) {
                    if (substitution.getStatus().equals(Substitution.STATUS_NOT_SYNCHRONIZED)) {
                        pendingSubstitutions.add(substitution);
                    }
                }
            }

            int progress = 0;
            LinkedList<SimpleServerReply> replies = new LinkedList<>();
            for (Substitution substitution : pendingSubstitutions) {
                Result result = steRepository.sendSubstitution(substitution);
                if (result instanceof Result.Success) {
                    SimpleServerReply reply = ((Result.Success<SimpleServerReply>) result).getData();
                    replies.add(reply);
                    if (reply.getStatus().equals("ok")) {
                        steRepository.setSubstitutionStatus(substitution.getID(),
                                Substitution.STATUS_SYNCHRONIZED);
                    } else {
                        steRepository.setSubstitutionStatus(substitution.getID(),
                                Substitution.STATUS_ERROR);
                    }
                } else {
                    steRepository.setSubstitutionStatus(substitution.getID(),
                            Substitution.STATUS_ERROR);
                }
                progress += 100 / pendingSubstitutions.size();
                publishProgress(progress);
            }
            return replies;
        }

        @Override
        protected void onPostExecute(List<SimpleServerReply> simpleServerReplies) {
            super.onPostExecute(simpleServerReplies);
            syncProgress.setValue(-1);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            syncProgress.setValue(values[0]);
            Log.d(TAG, "onProgressUpdate: Progress " + values[0]);
        }
    }
}
