package ru.wsr.stemobile.ui.main;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ru.wsr.stemobile.data.STERepository;
import ru.wsr.stemobile.data.model.Substitution;

public class MainViewModel extends AndroidViewModel {
    private STERepository steRepository;

    private LiveData<List<Substitution>> substitutionsList;

    public MainViewModel(@NonNull Application application) {
        super(application);
        steRepository = STERepository.getRepository(application.getApplicationContext());
        substitutionsList = steRepository.getAllSubstitutions();
        Log.d("stemobile", "MainViewModel: requested substitutions");
    }

    LiveData<List<Substitution>> getSubstitutionsList() {
        return substitutionsList;
    }
}
