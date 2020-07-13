package ru.mrlargha.stemobile.ui.substitutionadd

class SubstitutionAddFormStateBuilder {
    var dateError: String? = null
        private set
    var cabinetError: String? = null
        private set
    var groupError: String? = null
        private set
    val pairError: String? = null
    var teacherError: String? = null
        private set
    var subjectError: String? = null
        private set
    private var mHasErrors = false
    var customError: String? = null
        private set

    fun setDateError(dateError: String?): SubstitutionAddFormStateBuilder {
        this.dateError = dateError
        mHasErrors = true
        return this
    }

    fun setCabinetError(cabinetError: String?): SubstitutionAddFormStateBuilder {
        this.cabinetError = cabinetError
        mHasErrors = true
        return this
    }

    fun setGroupError(groupError: String?): SubstitutionAddFormStateBuilder {
        this.groupError = groupError
        mHasErrors = true
        return this
    }

    fun setTeacherError(teacherError: String?): SubstitutionAddFormStateBuilder {
        this.teacherError = teacherError
        mHasErrors = true
        return this
    }

    fun setSubjectError(subjectError: String?): SubstitutionAddFormStateBuilder {
        this.subjectError = subjectError
        mHasErrors = true
        return this
    }

    fun hasErrors(): Boolean {
        return mHasErrors
    }

    fun setHasErrors(hasErrors: Boolean): SubstitutionAddFormStateBuilder {
        mHasErrors = hasErrors
        return this
    }

    fun createSubstitutionAddFormState(): SubstitutionAddFormState {
        return SubstitutionAddFormState(dateError, cabinetError, groupError, teacherError,
                subjectError, customError, mHasErrors)
    }

    fun setCustomError(customError: String?): SubstitutionAddFormStateBuilder {
        this.customError = customError
        mHasErrors = true
        return this
    }
}