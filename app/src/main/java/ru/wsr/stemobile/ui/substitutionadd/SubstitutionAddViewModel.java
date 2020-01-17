package ru.wsr.stemobile.ui.substitutionadd;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import ru.wsr.stemobile.model.Substitution;
import ru.wsr.stemobile.repository.STERepository;
import ru.wsr.stemobile.tools.STEDateValidator;

public class SubstitutionAddViewModel extends AndroidViewModel {

    // Errors
    private MutableLiveData<String> dateError = new MutableLiveData<>("");
    private MutableLiveData<String> pairError = new MutableLiveData<>("");
    private MutableLiveData<String> groupError = new MutableLiveData<>("");
    private MutableLiveData<String> cabError = new MutableLiveData<>("");
    private MutableLiveData<String> teacherError = new MutableLiveData<>("");
    STERepository steRepository;
    private MutableLiveData<String> subjectError = new MutableLiveData<>("");
    private MutableLiveData<String> submitError = new MutableLiveData<>("");

    // Main app data
    private MutableLiveData<String> date = new MutableLiveData<>("");
    private MutableLiveData<String> cabinet = new MutableLiveData<>("");
    private MutableLiveData<String> group = new MutableLiveData<>("");
    private MutableLiveData<String> pair = new MutableLiveData<>("");
    private MutableLiveData<String> newSubject = new MutableLiveData<>("");
    private MutableLiveData<String> newTeacher = new MutableLiveData<>("");
    private MutableLiveData<String> oldSubject = new MutableLiveData<>("");
    private MutableLiveData<String> oldTeacher = new MutableLiveData<>("");
    private MutableLiveData<ArrayList<String>> teachersList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> subjectsList = new MutableLiveData<>();

    public SubstitutionAddViewModel(@NonNull Application application) {
        super(application);
        if (teachersList.getValue() == null) {
            loadTeachers();
        }

        if (subjectsList.getValue() == null) {
            loadSubjects();
        }

        if (groupsList.isEmpty()) {
            loadGroups();
        }

        steRepository = STERepository.getRepository(application.getApplicationContext());
    }

    //Local-only data
    private ArrayList<String> groupsList = new ArrayList<>();

    public MutableLiveData<String> getSubmitError() {
        return submitError;
    }

    public MutableLiveData<ArrayList<String>> getSubjectsList() {
        return subjectsList;
    }

    public MutableLiveData<String> getGroupError() {
        return groupError;
    }

    public MutableLiveData<String> getCabError() {
        return cabError;
    }

    public MutableLiveData<String> getTeacherError() {
        return teacherError;
    }

    public MutableLiveData<String> getPair() {
        return pair;
    }

    public MutableLiveData<String> getDate() {
        return date;
    }

    public MutableLiveData<String> getCabinet() {
        return cabinet;
    }

    public void setPair(String newPair) {
        if (!newPair.isEmpty()) {
            int pairNumber = Integer.parseInt(newPair);
            if (pairNumber >= 1 && pairNumber <= 6) {
                pair.setValue(newPair);
                setError(pairError, pair, "");
            } else {
                setError(pairError, pair, "Неверный номер пары");
            }
        }
    }

    public MutableLiveData<String> getGroup() {
        return group;
    }

    public void setGroup(String newGroup) {
        boolean containsNewGroup = false;
        for (String elem : groupsList) {
            if (newGroup.equals(elem)) {
                containsNewGroup = true;
                break;
            }
        }
        if (containsNewGroup) {
            group.setValue(newGroup);
            setError(groupError, group, "");
        } else {
            setError(groupError, group, "Группа не найдена");
        }
        requestOldTeacherAndSubject();
    }

    public MutableLiveData<String> getNewSubject() {
        return newSubject;
    }

    private void setError(MutableLiveData<String> errorLiveData, MutableLiveData<String> field,
                          String error) {
        if (error.isEmpty()) {
            errorLiveData.setValue("");
        } else {
            errorLiveData.setValue(error);
            field.setValue("");
        }
    }

    public boolean hasErrors() {
        try {
            return !(dateError.getValue().isEmpty() || pairError.getValue().isEmpty() ||
                    groupError.getValue().isEmpty() || cabError.getValue().isEmpty() ||
                    teacherError.getValue().isEmpty() || subjectError.getValue().isEmpty());
        } catch (NullPointerException e) {
            return true;
        }
    }

    public boolean checkEmptyFields() {
        try {
            return date.getValue().isEmpty() || pair.getValue().isEmpty() || group.getValue().isEmpty()
                    || cabinet.getValue().isEmpty() || newTeacher.getValue().isEmpty()
                    || newSubject.getValue().isEmpty();
        } catch (NullPointerException e) {
            return true;
        }
    }

    public void submit() {
        if (checkEmptyFields()) {
            submitError.setValue("Заполните все поля!");
        } else if (hasErrors()) {
            submitError.setValue("Данные содержат ошибки");
        } else {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
            try {
                Substitution substitution = new Substitution(newTeacher.getValue(), newSubject.getValue(),
                                                             Integer.parseInt(group.getValue()),
                                                             Integer.parseInt(pair.getValue()),
                                                             sdf.parse(date.getValue()), cabinet.getValue());
                steRepository.insertSubstitutionToDB(substitution);
                submitError.setValue("");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestOldTeacherAndSubject() {
        if (date.getValue() != null && pair.getValue() != null && group.getValue() != null &&
                !date.getValue().isEmpty() && !group.getValue().isEmpty() && pair.getValue().isEmpty()
                && !hasErrors()) {
            // TODO: Implement timetable (Each, it is a great challenge!)
            oldTeacher.setValue("Замещаемый И.О.");
            oldSubject.setValue("Какой-то предмет");
            Log.d("stemobile", "requestOldTeacherAndSubject: твоя жопа");
        } else {
            oldTeacher.setValue("");
            oldSubject.setValue("");
        }
    }

    public void setCabinet(String newCabinet) {
        cabinet.setValue(newCabinet);
    }

    void setDate(String date) {
        if (!date.isEmpty()) {
            try {
                if (date.length() != 8) {
                    throw new ParseException("Fuck up!", 0);
                }
                @SuppressLint("SimpleDateFormat") Date parsedDate = new SimpleDateFormat("dd.MM.yy").parse(date);
                if (!new STEDateValidator(Calendar.getInstance().getTimeInMillis()).isValid(parsedDate.getTime())) {
                    setError(dateError, this.date, "Невозможно установить дату " + date + "!");
                } else {
                    this.date.setValue(date);
                }
            } catch (ParseException e) {
                setError(dateError, this.date, "Введите дату в формате ДД.ММ.ГГ");
            } catch (NullPointerException e) {
                setError(dateError, this.date, "Что за дичь вы сумели ввести?");
            }
        } else {
            setError(dateError, this.date, "Введите дату в формате ДД.ММ.ГГ");
        }
        requestOldTeacherAndSubject();
    }

    public MutableLiveData<String> getNewTeacher() {
        return newTeacher;
    }

    public MutableLiveData<String> getOldSubject() {
        return oldSubject;
    }

    public MutableLiveData<String> getOldTeacher() {
        return oldTeacher;
    }

    public LiveData<String> getDateError() {
        return dateError;
    }

    public MutableLiveData<String> getPairError() {
        return pairError;
    }

    public MutableLiveData<ArrayList<String>> getTeachersList() {
        return teachersList;
    }

    public void init() {

    }

    private void loadTeachers() {
        ArrayList<String> teachers = new ArrayList<>(Arrays.asList("Густова Т.А", "Юрьева И.А",
                                                                   "Рохманько И.Л", "Горбунова О.А", "Опалева У.С", "Куликов Д.Д"));
        teachersList.setValue(teachers);
    }

    private void loadSubjects() {
        ArrayList<String> teachers = new ArrayList<>(Arrays.asList("МДК 01.01", "МДК 01.02",
                                                                   "Математика", "Информатика", "Литература", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."));
        subjectsList.setValue(teachers);
    }

    private void loadGroups() {
        groupsList = new ArrayList<>(Arrays.asList("822", "821", "724"));
    }

    public MutableLiveData<String> getSubjectError() {
        return subjectError;
    }


}
