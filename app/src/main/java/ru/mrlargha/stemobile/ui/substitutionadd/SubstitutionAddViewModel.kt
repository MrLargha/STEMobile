package ru.mrlargha.stemobile.ui.substitutionadd

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.mrlargha.stemobile.data.LoginRepository.Companion.getInstance
import ru.mrlargha.stemobile.data.Result
import ru.mrlargha.stemobile.data.STEDataSource
import ru.mrlargha.stemobile.data.STERepository
import ru.mrlargha.stemobile.data.STERepository.Companion.getRepository
import ru.mrlargha.stemobile.data.model.Substitution
import ru.mrlargha.stemobile.data.model.SubstitutionFormHints
import ru.mrlargha.stemobile.tools.DateFormatter
import ru.mrlargha.stemobile.tools.STEDateValidator
import java.text.ParseException
import java.util.*

class SubstitutionAddViewModel(application: Application) : AndroidViewModel(application) {
    private val steRepository: STERepository?
    val formState = MutableLiveData<SubstitutionAddFormState>()
    val teachersList = MutableLiveData<ArrayList<String>>()
    val subjectsList = MutableLiveData<ArrayList<String>>()
    private var groupsList = ArrayList<String>()
    val localSubstitutionsLiveData: LiveData<List<Substitution>>
    var localSubstitutions: List<Substitution> = LinkedList()
    private fun isDateValid(date: String?): Boolean {
        if (date == null) {
            return false
        }
        return if (!date.isEmpty()) {
            try {
                if (date.length != 8) {
                    return false
                }
                val parsedDate = DateFormatter.stringToDate(date)
                STEDateValidator(Calendar.getInstance().timeInMillis).isValid(parsedDate!!.time)
            } catch (e: ParseException) {
                false
            } catch (e: NullPointerException) {
                false
            }
        } else {
            false
        }
    }

    private fun isGroupValid(group: String): Boolean {
        for (elem in groupsList) {
            if (group == elem) {
                return true
            }
        }
        return false
    }

    private fun parseGroups(rawString: String): ArrayList<String> {
        val groups = rawString.split(" ".toRegex()).toTypedArray()
        val result = ArrayList<String>()
        for (group in groups) {
            if (isGroupValid(group)) {
                result.add(group)
            }
        }
        return result
    }

    private fun isCabinetValid(cabinet: String?): Boolean {
        return !(cabinet == null || cabinet.isEmpty())
    }

    fun submitSubstitution(date: String?, pair: String,
                           rawGroups: String, cabinet: String?,
                           teacher: String, subject: String) {
        val builder = SubstitutionAddFormStateBuilder()
        val groups = parseGroups(rawGroups)
        if (!isDateValid(date)) {
            builder.setDateError("Неверная дата")
        }
        if (groups.isEmpty()) {
            builder.setGroupError("Группа не найдена")
        }
        if (!isCabinetValid(cabinet)) {
            builder.setCabinetError("Введите кабинент")
        }
        if (teacher.isEmpty()) {
            builder.setTeacherError("Введите ФИО преподавателя!")
        }
        if (subject.isEmpty()) {
            builder.setSubjectError("Введите название предмета")
        }
        if (pair.toInt() < 1) {
            builder.setCustomError("Выберите номер пары")
        }
        if (!builder.hasErrors()) {
            for (group in groups) {
                try {
                    val substitution = Substitution(teacher, subject, group.toInt(), pair.toInt(),
                            DateFormatter.stringToDate(date!!)!!,
                            cabinet!!, Substitution.STATUS_NOT_SYNCHRONIZED,
                            getInstance(STEDataSource())!!.name!!)
                    var isOk = true
                    for (localSubstitution in localSubstitutions) {
                        if (localSubstitution.equals(substitution)) {
                            isOk = false
                            break
                        }
                    }
                    if (isOk) {
                        steRepository!!.insertSubstitutionToDB(substitution)
                    } else {
                        builder.setCustomError("Такое замещение уже добавлено")
                        break
                    }
                } catch (e: ParseException) {
                    builder.setDateError("Неверный формат даты.")
                    break
                }
            }
        }
        formState.value = builder.createSubstitutionAddFormState()
    }

    private inner class HintsFetchTask : AsyncTask<Void?, Void?, SubstitutionFormHints?>() {
        override fun doInBackground(vararg params: Void?): SubstitutionFormHints? {
            val result: Result<*> = steRepository!!.formHints
            return if (result is Result.Success<*>) {
                result.data as SubstitutionFormHints?
            } else {
                null
            }
        }

        override fun onPostExecute(substitutionFormHints: SubstitutionFormHints?) {
            super.onPostExecute(substitutionFormHints)
            if (substitutionFormHints != null) {
                subjectsList.postValue(ArrayList(substitutionFormHints.subjects))
                groupsList = ArrayList(substitutionFormHints.groups)
                teachersList.postValue(ArrayList(substitutionFormHints.teachers))
            }
        }
    }

    init {
        steRepository = getRepository(application.applicationContext)
        localSubstitutionsLiveData = steRepository!!.substitutions
        HintsFetchTask().execute()
    }
}