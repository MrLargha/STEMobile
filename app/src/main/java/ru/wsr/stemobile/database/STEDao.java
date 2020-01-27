package ru.wsr.stemobile.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.List;

import ru.wsr.stemobile.data.model.Substitution;

@Dao
public interface STEDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubstitution(Substitution substitution);

    @Query("SELECT * FROM substitutions_table ORDER BY substitutionDate, `group`, pair")
    LiveData<List<Substitution>> getAllSubstitutions();

    @Query("DELETE FROM substitutions_table")
    void deleteAll();

    @TypeConverters({DateConverter.class})
    @Query("SELECT * FROM substitutions_table WHERE `group`= :group AND pair = :pair" +
            " AND substitutionDate=:date")
    LiveData<List<Substitution>> getSubstitutions(int group, int pair, Date date);

    @Query("DELETE FROM substitutions_table WHERE uid = :uid")
    void deleteByUID(long uid);
}
