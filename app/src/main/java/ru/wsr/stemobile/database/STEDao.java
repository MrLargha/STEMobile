package ru.wsr.stemobile.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ru.wsr.stemobile.model.Substitution;

@Dao
public interface STEDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubstitution(Substitution substitution);

    @Query("SELECT * FROM substitutions_table ORDER BY uid")
    LiveData<List<Substitution>> getAllSubstitutions();

    @Query("DELETE FROM substitutions_table")
    void deleteAll();
}
