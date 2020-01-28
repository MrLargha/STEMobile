package ru.mrlargha.stemobile.ui.substitutionadd;

import androidx.annotation.Nullable;

public class SubstitutionAddFormState {
    private @Nullable
    String dateError;
    private @Nullable
    String cabinetError;
    private @Nullable
    String groupError;
    private @Nullable
    String teacherError;
    private @Nullable
    String subjectError;

    @Nullable
    private String customError;

    private boolean hasErrors;

    SubstitutionAddFormState(@Nullable String dateError, @Nullable String cabinetError,
                             @Nullable String groupError, @Nullable String teacherError,
                             @Nullable String subjectError, @Nullable String customError,
                             boolean hasErrors) {
        this.dateError = dateError;
        this.cabinetError = cabinetError;
        this.groupError = groupError;
        this.teacherError = teacherError;
        this.subjectError = subjectError;
        this.customError = customError;
        this.hasErrors = hasErrors;
    }

    public SubstitutionAddFormState(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    @Nullable
    public String getDateError() {
        return dateError;
    }

    @Nullable
    public String getCabinetError() {
        return cabinetError;
    }

    @Nullable
    public String getGroupError() {
        return groupError;
    }

    @Nullable
    public String getTeacherError() {
        return teacherError;
    }

    @Nullable
    public String getSubjectError() {
        return subjectError;
    }

    @Nullable
    public String getCustomError() {
        return customError;
    }

    public boolean hasErrors() {
        return hasErrors;
    }


}
