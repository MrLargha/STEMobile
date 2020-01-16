package ru.wsr.stemobile.database;

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

import java.util.Date;
import java.util.List;

import ru.wsr.stemobile.LiveDataTestUtil;
import ru.wsr.stemobile.model.Substitution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


@RunWith(AndroidJUnit4.class)
public class SubstitutionDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    private STERoomDatabase mDatabase;
    private SubstitutionDao mSubstitutionDao;

    @Before
    public void connectToDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        mDatabase = Room.inMemoryDatabaseBuilder(context, STERoomDatabase.class)
                .allowMainThreadQueries()
                .build();
        mSubstitutionDao = mDatabase.substitutionDao();
    }

    @After
    public void closeDB() {
        mDatabase.close();
    }

    @Test
    public void insertSubstitutionAndCheckIt() throws Exception {
        Substitution substitution = new Substitution("Юрьева И.А", "МДК 01.01",
                822, 1, new Date(), "522");

        mSubstitutionDao.insertSubstitution(substitution);

        List<Substitution> result = LiveDataTestUtil.getValue(
                mSubstitutionDao.getAllSubstitutions());
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
        mSubstitutionDao.deleteAll();
        List<Substitution> result = LiveDataTestUtil.getValue(
                mSubstitutionDao.getAllSubstitutions());
        assertEquals(0, result.size());
    }
}