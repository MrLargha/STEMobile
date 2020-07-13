package ru.mrlargha.stemobile.ui.substitutionadd

class SubstitutionAddFormState internal constructor(dateError: String?, cabinetError: String?,
                                                    groupError: String?, teacherError: String?,
                                                    subjectError: String?, customError: String?,
                                                    private var hasErrors: Boolean) {
    var dateError: String? = dateError
        private set
    var cabinetError: String? = cabinetError
        private set
    var groupError: String? = groupError
        private set
    var teacherError: String? = teacherError
        private set
    var subjectError: String? = subjectError
        private set
    var customError: String? = customError
        private set

    fun hasErrors(): Boolean {
        return hasErrors
    }
}