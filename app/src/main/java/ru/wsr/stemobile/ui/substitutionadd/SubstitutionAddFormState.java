package ru.wsr.stemobile.ui.substitutionadd;

import androidx.annotation.Nullable;

public class SubstitutionAddFormState {
    private @Nullable
    String dateError;
    private @Nullable
    String cabinetError;
    private @Nullable
    String groupError;
    private @Nullable
    String pairError;
    private @Nullable
    String teacherError;
    private @Nullable
    String subjectError;
    private boolean hasErrors;


    public SubstitutionAddFormState(@Nullable String dateError, @Nullable String cabinetError,
                                    @Nullable String groupError, @Nullable String pairError,
                                    @Nullable String teacherError, @Nullable String subjectError,
                                    boolean hasErrors) {
        this.dateError = dateError;
        this.cabinetError = cabinetError;
        this.groupError = groupError;
        this.pairError = pairError;
        this.teacherError = teacherError;
        this.subjectError = subjectError;
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
    public String getPairError() {
        return pairError;
    }

    @Nullable
    public String getTeacherError() {
        return teacherError;
    }

    @Nullable
    public String getSubjectError() {
        return subjectError;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }


}
