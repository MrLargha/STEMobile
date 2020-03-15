package ru.mrlargha.stemobile.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.mrlargha.stemobile.data.model.Substitution;

public class SubstitutionsSort {
    public static ArrayList<Substitution> moveOldDatesToEnd(ArrayList<Substitution> substitutions,
                                                            Date referenceDate) {
        ArrayList<Substitution> actualSubstitutions = new ArrayList<>();
        ArrayList<Substitution> irrelevantSubstitutions = new ArrayList<>();
        Calendar reference = Calendar.getInstance();
        reference.setTime(referenceDate);
        reference.set(Calendar.HOUR_OF_DAY, reference.getActualMinimum(Calendar.HOUR_OF_DAY));
        reference.set(Calendar.MINUTE, reference.getActualMinimum(Calendar.MINUTE));
        reference.set(Calendar.SECOND, reference.getActualMinimum(Calendar.SECOND));
        reference.set(Calendar.MILLISECOND, reference.getActualMinimum(Calendar.MILLISECOND));

        Date clearedReferenceDate = reference.getTime();

        for (Substitution substitution :
                substitutions) {
            if (clearedReferenceDate.before(substitution.getSubstitutionDate())
                    || clearedReferenceDate.equals(substitution.getSubstitutionDate())) {
                actualSubstitutions.add(substitution);
            } else {
                irrelevantSubstitutions.add(substitution);
            }
        }

        actualSubstitutions.addAll(irrelevantSubstitutions);

        return actualSubstitutions;
    }
}
