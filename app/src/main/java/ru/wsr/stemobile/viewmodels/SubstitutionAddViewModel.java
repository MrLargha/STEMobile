package ru.wsr.stemobile.viewmodels;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import ru.wsr.stemobile.STEDateValidator;

public class SubstitutionAddViewModel extends ViewModel {

    // Errors
    private MutableLiveData<String> dateError = new MutableLiveData<>("");
    private MutableLiveData<String> pairError = new MutableLiveData<>("");
    private MutableLiveData<String> groupError = new MutableLiveData<>("");
    private MutableLiveData<String> cabError = new MutableLiveData<>("");
    private MutableLiveData<String> teacherError = new MutableLiveData<>("");

    // Main app data
    private MutableLiveData<String> date = new MutableLiveData<>("");
    private MutableLiveData<String> cabinet = new MutableLiveData<>("");
    private MutableLiveData<String> group = new MutableLiveData<>("");


    private MutableLiveData<Integer> pair = new MutableLiveData<>();
    private MutableLiveData<String> newSubject = new MutableLiveData<>("");
    private MutableLiveData<String> newTeacher = new MutableLiveData<>("");
    private MutableLiveData<String> oldSubject = new MutableLiveData<>("");
    private MutableLiveData<String> oldTeacher = new MutableLiveData<>("");
    private MutableLiveData<ArrayList<String>> teachersList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> subjectsList = new MutableLiveData<>();

    //Local-only data
    private ArrayList<String> groupsList = new ArrayList<>();

    public MutableLiveData<ArrayList<String>> getSubjectsList() {
        return subjectsList;
    }

    public MutableLiveData<String> getGroupError() {
        return groupError;
    }

    public MutableLiveData<String> getCabError() {
        return cabError;
    }

    public MutableLiveData<Integer> getPair() {
        return pair;
    }

    public MutableLiveData<String> getTeacherError() {
        return teacherError;
    }

    public MutableLiveData<String> getDate() {
        return date;
    }

    public void setPair(int newPair) {
        if (newPair > 0 && newPair <= 6) {
            pair.setValue(newPair);
            pairError.setValue("");
        } else {
            pairError.setValue("Неверный номер пары");
        }
    }

    public MutableLiveData<String> getCabinet() {
        return cabinet;
    }

    public void setCabinet(String newCabinet) {
        cabinet.setValue(newCabinet);
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
            groupError.setValue("");
        } else {
            groupError.setValue("Группа не найдена");
        }
    }

    public MutableLiveData<String> getNewSubject() {
        return newSubject;
    }

    public void setDate(String date) {
        if (!date.isEmpty())
            try {
                if (date.length() != 8) {
                    throw new ParseException("Fuck up!", 0);
                }
                @SuppressLint("SimpleDateFormat") Date parsedDate = new SimpleDateFormat("dd.MM.yy").parse(date);
                if (!new STEDateValidator(Calendar.getInstance().getTimeInMillis()).isValid(parsedDate.getTime())) {
                    dateError.setValue("Невозможно установить дату " + date + "!");
                } else {
                    dateError.setValue("");
                }
            } catch (ParseException e) {
                dateError.setValue("Введите дату в формате ДД.ММ.ГГ");
            } catch (NullPointerException e) {
                dateError.setValue("Что за дичь вы сумели ввести?");
            }
        else
            dateError.setValue("");
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
        if (dateError == null) {
            dateError = new MutableLiveData<>();
        }
        return dateError;
    }

    public MutableLiveData<String> getPairError() {
        return pairError;
    }

    public MutableLiveData<ArrayList<String>> getTeachersList() {
        return teachersList;
    }

    public void init() {
        if (teachersList.getValue() == null) {
            loadTeachers();
        }

        if (subjectsList.getValue() == null) {
            loadSubjects();
        }

        if (groupsList.isEmpty()) {
            loadGroups();
        }
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

}
