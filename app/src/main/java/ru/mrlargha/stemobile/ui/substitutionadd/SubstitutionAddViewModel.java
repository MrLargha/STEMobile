package ru.mrlargha.stemobile.ui.substitutionadd;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ru.mrlargha.stemobile.data.LoginRepository;
import ru.mrlargha.stemobile.data.Result;
import ru.mrlargha.stemobile.data.STEDataSource;
import ru.mrlargha.stemobile.data.STERepository;
import ru.mrlargha.stemobile.data.model.Substitution;
import ru.mrlargha.stemobile.data.model.SubstitutionFormHints;
import ru.mrlargha.stemobile.tools.DateFormatter;
import ru.mrlargha.stemobile.tools.STEDateValidator;

public class SubstitutionAddViewModel extends AndroidViewModel {
    private STERepository steRepository;

    private MutableLiveData<SubstitutionAddFormState> formState = new MutableLiveData<>();

    private MutableLiveData<ArrayList<String>> teachersList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> subjectsList = new MutableLiveData<>();
    private ArrayList<String> groupsList = new ArrayList<>();
    private LiveData<List<Substitution>> localSubstitutionsLiveData;

    private List<Substitution> localSubstitutions = new LinkedList<>();


    public SubstitutionAddViewModel(@NonNull Application application) {
        super(application);
        steRepository = STERepository.getRepository(application.getApplicationContext());
        localSubstitutionsLiveData = steRepository.getSubstitutions();

        new HitsFetchTask().execute();
    }

    private boolean isDateValid(String date) {
        if (date == null) {
            return false;
        }
        if (!date.isEmpty()) {
            try {
                if (date.length() != 8) {
                    return false;
                }
                Date parsedDate = DateFormatter.stringToDate(date);
                return new STEDateValidator(Calendar.getInstance().getTimeInMillis()).isValid(parsedDate.getTime());
            } catch (ParseException | NullPointerException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isGroupValid(String group) {
        for (String elem : groupsList) {
            if (group.equals(elem)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCabinetValid(String cabinet) {
        return !(cabinet == null || cabinet.isEmpty());
    }

    void submitSubstitution(String date, String pair,
                            String group, String cabinet,
                            String teacher, String subject) {
        SubstitutionAddFormStateBuilder builder = new SubstitutionAddFormStateBuilder();
        if (!isDateValid(date)) {
            builder.setDateError("Неверная дата");
        }
        if (!isGroupValid(group)) {
            builder.setGroupError("Группа не найдена");
        }
        if (!isCabinetValid(cabinet)) {
            builder.setCabinetError("Введите кабинент");
        }
        if (teacher.isEmpty()) {
            builder.setTeacherError("Введите ФИО преподавателя!");
        }
        if (subject.isEmpty()) {
            builder.setSubjectError("Введите название предмета");
        }
        if (Integer.parseInt(pair) < 1) {
            builder.setCustomError("Выберите номер пары");
        }
        if (!builder.hasErrors()) {
            try {
                Substitution substitution =
                        new Substitution(teacher, subject, Integer.parseInt(group),
                                         Integer.parseInt(pair), DateFormatter.stringToDate(date),
                                         cabinet, Substitution.STATUS_NOT_SYNCHRONIZED,
                                         LoginRepository.getInstance(new STEDataSource()).getName());
                boolean isOk = true;
                for (Substitution localSubstitution : localSubstitutions) {
                    if (localSubstitution.equals(substitution)) {
                        isOk = false;
                        break;
                    }
                }
                if (isOk) {
                    steRepository.insertSubstitutionToDB(substitution);
                } else {
                    builder.setCustomError("Такое замещение уже добавлено");
                }
            } catch (ParseException e) {
                builder.setDateError("Неверный формат даты.");
            }
        }
        formState.setValue(builder.createSubstitutionAddFormState());
    }

    MutableLiveData<ArrayList<String>> getSubjectsList() {
        return subjectsList;
    }

    MutableLiveData<SubstitutionAddFormState> getFormState() {
        return formState;
    }

    MutableLiveData<ArrayList<String>> getTeachersList() {
        return teachersList;
    }

    LiveData<List<Substitution>> getLocalSubstitutionsLiveData() {
        return localSubstitutionsLiveData;
    }

    public List<Substitution> getLocalSubstitutions() {
        return localSubstitutions;
    }

    void setLocalSubstitutions(List<Substitution> localSubstitutions) {
        this.localSubstitutions = localSubstitutions;
    }

    private class HitsFetchTask extends AsyncTask<Void, Void, SubstitutionFormHints> {

        @Override
        protected SubstitutionFormHints doInBackground(Void... voids) {
            Result<SubstitutionFormHints> result = steRepository.getFormHints();
            if (result instanceof Result.Success) {
                return (SubstitutionFormHints) ((Result.Success) result).getData();
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(SubstitutionFormHints substitutionFormHints) {
            super.onPostExecute(substitutionFormHints);
            if (substitutionFormHints != null) {
                subjectsList.postValue(new ArrayList<>(substitutionFormHints.getSubjects()));
                groupsList = new ArrayList<>(substitutionFormHints.getGroups());
                teachersList.postValue(new ArrayList<>(substitutionFormHints.getTeachers()));
            }
        }
    }

}
