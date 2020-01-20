package ru.wsr.stemobile.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import ru.wsr.stemobile.data.model.Substitution;

public class SubstitutionsSort {
    public static ArrayList<Substitution> sortSubstitutions(ArrayList<Substitution> substitutions) {
        Collections.sort(substitutions, (o1, o2) -> {
            if (o1.getSubstitutionDate().equals(o2.getSubstitutionDate())) {
                if (o1.getGroup() == o2.getGroup()) {
                    //noinspection ComparatorMethodParameterNotUsed
                    return o1.getPair() > o2.getPair() ? 1 : -1;
                } else {
                    return o1.getGroup() > o2.getGroup() ? 1 : -1;
                }
            } else {
                return o1.getSubstitutionDate().after(o2.getSubstitutionDate()) ? 1 : -1;
            }
        });
        return substitutions;
    }

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
