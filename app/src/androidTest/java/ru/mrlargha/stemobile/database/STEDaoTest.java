package ru.mrlargha.stemobile.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ru.mrlargha.stemobile.data.model.Substitution;


@RunWith(AndroidJUnit4.class)
public class STEDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    private STERoomDatabase mDatabase;
    private STEDao mSTEDao;

    @Before
    public void connectToDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        mDatabase = Room.inMemoryDatabaseBuilder(context, STERoomDatabase.class)
                .allowMainThreadQueries()
                .build();
        mSTEDao = mDatabase.substitutionDao();
    }

    @After
    public void closeDB() {
        mDatabase.close();
    }

    @Test
    public void insertSubstitutionAndCheckIt() throws Exception {
        Substitution substitution = new Substitution("Юрьева И.А", "МДК 01.01",
                822, 1, new Date(), "522", Substitution.STATUS_NOT_SYNCHRONIZED, "Author");

        mSTEDao.insertSubstitution(substitution);

        List<Substitution> result = LiveDataTestUtil.getValue(
                mSTEDao.getAllSubstitutions());
        assertNotEquals(0, result.size());

        //Check main fields of Substitution
        assertEquals(result.get(0).getSubstitutionDate(), substitution.getSubstitutionDate());
        assertEquals(result.get(0).getSubject(), substitution.getSubject());
        assertEquals(result.get(0).getTeacher(), substitution.getTeacher());
        assertEquals(result.get(0).getPair(), substitution.getPair());
        assertEquals(result.get(0).getCabinet(), substitution.getCabinet());

        // Check date converter
        assertEquals(result.get(0).getSubstitutionDate(), substitution.getSubstitutionDate());
    }

    @Test
    public void dropElementsFromDB() throws Exception {
        mSTEDao.deleteAll();
        List<Substitution> result = LiveDataTestUtil.getValue(
                mSTEDao.getAllSubstitutions());
        assertEquals(0, result.size());
    }

    @Test
    public void testAutoSort() throws Exception {
        mSTEDao.deleteAll();

        ArrayList<Substitution> original = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -2);
        for (int i = 0; i < 5; i++) {
            for (int g = 1; g < 3; g++) {
                for (int p = 1; p < 3; p++) {
                    original.add(new Substitution("Any teacher", "Any subject",
                            800 + g, p, c.getTime(), "Cabinet: 404", Substitution.STATUS_SYNCHRONIZED, "Author"));
                }
            }
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        ArrayList<Substitution> shuffled = (ArrayList<Substitution>) original.clone();
        Collections.shuffle(shuffled);

        for (Substitution substitution :
                shuffled) {
            mSTEDao.insertSubstitution(substitution);
        }

        List<Substitution> result = LiveDataTestUtil.getValue(
                mSTEDao.getAllSubstitutions());

        boolean isOk = true;
        for (int i = 0; i < result.size(); i++) {
            if (!result.get(i).equals(original.get(i))) {
                isOk = false;
                break;
            }
        }
        assert isOk;
    }
}