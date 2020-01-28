package ru.mrlargha.stemobile.ui.substitutionadd;

public class SubstitutionAddFormStateBuilder {
    private String mDateError = null;
    private String mCabinetError = null;
    private String mGroupError = null;
    private String mPairError = null;
    private String mTeacherError = null;
    private String mSubjectError = null;
    private boolean mHasErrors = false;
    private String mCustomError = null;

    public String getDateError() {
        return mDateError;
    }

    public SubstitutionAddFormStateBuilder setDateError(String dateError) {
        mDateError = dateError;
        mHasErrors = true;
        return this;
    }

    public String getCabinetError() {
        return mCabinetError;
    }

    public SubstitutionAddFormStateBuilder setCabinetError(String cabinetError) {
        mCabinetError = cabinetError;
        mHasErrors = true;
        return this;
    }

    public String getGroupError() {
        return mGroupError;
    }

    public SubstitutionAddFormStateBuilder setGroupError(String groupError) {
        mGroupError = groupError;
        mHasErrors = true;
        return this;
    }

    public String getPairError() {
        return mPairError;
    }

    public String getTeacherError() {
        return mTeacherError;
    }

    public SubstitutionAddFormStateBuilder setTeacherError(String teacherError) {
        mTeacherError = teacherError;
        mHasErrors = true;
        return this;
    }

    public String getSubjectError() {
        return mSubjectError;
    }

    public SubstitutionAddFormStateBuilder setSubjectError(String subjectError) {
        mSubjectError = subjectError;
        mHasErrors = true;
        return this;
    }

    public boolean hasErrors() {
        return mHasErrors;
    }

    public SubstitutionAddFormStateBuilder setHasErrors(boolean hasErrors) {
        mHasErrors = hasErrors;
        return this;
    }

    public SubstitutionAddFormState createSubstitutionAddFormState() {
        return new SubstitutionAddFormState(mDateError, mCabinetError, mGroupError, mTeacherError,
                mSubjectError, mCustomError, mHasErrors);
    }

    public String getCustomError() {
        return mCustomError;
    }

    public SubstitutionAddFormStateBuilder setCustomError(String customError) {
        mCustomError = customError;
        mHasErrors = true;
        return this;
    }
}