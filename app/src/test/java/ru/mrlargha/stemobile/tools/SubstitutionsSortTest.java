//package ru.mrlargha.stemobile.tools;
//
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Date;
//
//import ru.mrlargha.stemobile.data.model.Substitution;
//
//public class SubstitutionsSortTest {
//
//    @Test
//    public void testSortingOfSubstitutions() {
//        ArrayList<Substitution> original = new ArrayList<>();
//        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
//        c.add(Calendar.DAY_OF_YEAR, -2);
//        for (int i = 0; i < 5; i++) {
//            for (int g = 1; g < 3; g++) {
//                for (int p = 1; p < 3; p++) {
//                    original.add(new Substitution("Any teacher", "Any subject",
//                                                  800 + g, p, c.getTime(), "Cabinet: 404", Substitution.STATUS_NOT_SYNCHRONIZED, ));
//                }
//            }
//            c.add(Calendar.DAY_OF_YEAR, 1);
//        }
//
//        ArrayList<Substitution> shuffled = (ArrayList<Substitution>) original.clone();
//        Collections.shuffle(shuffled);
//        SubstitutionsSort.sortSubstitutions(shuffled);
//        assert compareLists(original, shuffled);
//    }
//
//    @Test
//    public void testMoveOldDatesToEnd() {
//
//        ArrayList<Substitution> original = new ArrayList<>();
//        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
//        for (int i = 0; i < 5; i++) {
//            for (int g = 1; g < 3; g++) {
//                for (int p = 1; p < 3; p++) {
//                    original.add(new Substitution("Any teacher", "Any subject",
//                                                  800 + g, p, c.getTime(), "Cabinet: 404", Substitution.STATUS_NOT_SYNCHRONIZED, ));
//                }
//            }
//            if (i < 4)
//                c.add(Calendar.DAY_OF_YEAR, 1);
//            else
//                c.add(Calendar.DAY_OF_YEAR, -10);
//        }
//
//        ArrayList<Substitution> movedDates = (ArrayList<Substitution>) original.clone();
//        SubstitutionsSort.moveOldDatesToEnd(movedDates, new Date());
//
//
//        assert compareLists(original, movedDates);
//    }
//
//    private boolean compareLists(ArrayList<Substitution> list1, ArrayList<Substitution> list2) {
//        boolean isOk = true;
//        for (int i = 0; i < list1.size(); i++) {
//            if (!list1.get(i).equals(list2.get(i))) {
//                isOk = false;
//                break;
//            }
//        }
//        return isOk;
//    }
//}