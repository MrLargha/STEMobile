package ru.wsr.stemobile.ui.substitutionadd;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;

import ru.wsr.stemobile.repository.STERepository;

public class SubstitutionAddViewModel extends AndroidViewModel {

    private LiveData<SubstitutionAddFormState> formState;

    // Main form data
    private STERepository steRepository;
    private MutableLiveData<String> oldSubject = new MutableLiveData<>("");
    private MutableLiveData<String> oldTeacher = new MutableLiveData<>("");
    private MutableLiveData<ArrayList<String>> teachersList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> subjectsList = new MutableLiveData<>();
    private ArrayList<String> groupsList = new ArrayList<>();


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

    void substitutionDataChanged(String date, String pair, String group, String cabinet,
                                 String teacher, String subject) {

    }

    MutableLiveData<ArrayList<String>> getSubjectsList() {
        return subjectsList;
    }

    private void requestOldTeacherAndSubject() {
//        if (date.getValue() != null && pair.getValue() != null && group.getValue() != null &&
//                !date.getValue().isEmpty() && !group.getValue().isEmpty() && pair.getValue().isEmpty()
//                && !hasErrors()) {
//            // TODO: Implement timetable (Each, it is a great challenge!)
//            oldTeacher.setValue("Замещаемый И.О.");
//            oldSubject.setValue("Какой-то предмет");
//            Log.d("stemobile", "requestOldTeacherAndSubject: твоя жопа");
//        } else {
//            oldTeacher.setValue("");
//            oldSubject.setValue("");
//        }
    }

    void setDate(String date) {
//        if (!date.isEmpty()) {
//            try {
//                if (date.length() != 8) {
//                    throw new ParseException("Fuck up!", 0);
//                }
//                @SuppressLint("SimpleDateFormat") Date parsedDate = new SimpleDateFormat("dd.MM.yy").parse(date);
//                if (!new STEDateValidator(Calendar.getInstance().getTimeInMillis()).isValid(parsedDate.getTime())) {
//                    setError(dateError, this.date, "Невозможно установить дату " + date + "!");
//                } else {
//                    this.date.setValue(date);
//                }
//            } catch (ParseException e) {
//                setError(dateError, this.date, "Введите дату в формате ДД.ММ.ГГ");
//            } catch (NullPointerException e) {
//                setError(dateError, this.date, "Что за дичь вы сумели ввести?");
//            }
//        } else {
//            setError(dateError, this.date, "Введите дату в формате ДД.ММ.ГГ");
//        }
//        requestOldTeacherAndSubject();
    }

    MutableLiveData<String> getOldSubject() {
        return oldSubject;
    }

    MutableLiveData<String> getOldTeacher() {
        return oldTeacher;
    }

    MutableLiveData<ArrayList<String>> getTeachersList() {
        return teachersList;
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
