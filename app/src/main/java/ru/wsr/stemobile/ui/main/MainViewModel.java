package ru.wsr.stemobile.ui.main;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import ru.wsr.stemobile.data.STERepository;
import ru.wsr.stemobile.data.model.Substitution;

public class MainViewModel extends AndroidViewModel {
    private STERepository steRepository;

    private LiveData<List<Substitution>> substitutionsList;

    private MutableLiveData<String> undoString = new MutableLiveData<>();

    private ArrayList<Substitution> savedSubstitutions = new ArrayList<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        steRepository = STERepository.getRepository(application.getApplicationContext());
        substitutionsList = steRepository.getAllSubstitutions();
        Log.d("stemobile", "MainViewModel: requested substitutions");
    }

    LiveData<List<Substitution>> getSubstitutionsList() {
        return substitutionsList;
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
}
