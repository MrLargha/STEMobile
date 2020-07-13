package ru.mrlargha.stemobile.tools

import ru.mrlargha.stemobile.data.model.Substitution
import java.util.*

object SubstitutionsSort {
    fun moveOldDatesToEnd(substitutions: ArrayList<Substitution>,
                          referenceDate: Date?): ArrayList<Substitution> {
        val actualSubstitutions = ArrayList<Substitution>()
        val irrelevantSubstitutions = ArrayList<Substitution>()
        val reference = Calendar.getInstance()
        reference.time = referenceDate
        reference[Calendar.HOUR_OF_DAY] = reference.getActualMinimum(Calendar.HOUR_OF_DAY)
        reference[Calendar.MINUTE] = reference.getActualMinimum(Calendar.MINUTE)
        reference[Calendar.SECOND] = reference.getActualMinimum(Calendar.SECOND)
        reference[Calendar.MILLISECOND] = reference.getActualMinimum(Calendar.MILLISECOND)
        val clearedReferenceDate = reference.time
        for (substitution in substitutions) {
            if (clearedReferenceDate.before(substitution.substitutionDate)
                    || clearedReferenceDate == substitution.substitutionDate) {
                actualSubstitutions.add(substitution)
            } else {
                irrelevantSubstitutions.add(substitution)
            }
        }
        actualSubstitutions.addAll(irrelevantSubstitutions)
        return actualSubstitutions
    }
}