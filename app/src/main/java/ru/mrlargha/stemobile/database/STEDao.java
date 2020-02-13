package ru.mrlargha.stemobile.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.List;

import ru.mrlargha.stemobile.data.model.Substitution;

@Dao
public interface STEDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubstitution(Substitution substitution);

    @Query("SELECT * FROM substitutions_table ORDER BY substitutionDate, `group`, pair")
    LiveData<List<Substitution>> getAllSubstitutions();

    @Query("SELECT * FROM substitutions_table ORDER BY substitutionDate, `group`, pair")
    Substitution[] getAllSubstitutionsSync();

    @Query("DELETE FROM substitutions_table")
    void deleteAll();

    @TypeConverters({DateConverter.class})
    @Query("SELECT * FROM substitutions_table WHERE `group`= :group AND pair = :pair" +
            " AND substitutionDate=:date")
    LiveData<List<Substitution>> getSubstitutions(int group, int pair, Date date);

    @Query("DELETE FROM substitutions_table WHERE uid = :uid")
    void deleteByUID(long uid);

    @TypeConverters({DateConverter.class})
    @Query("SELECT * FROM substitutions_table WHERE status = 'NOT_SYNCHRONIZED'")
    Substitution[] getUnSyncSubstitutions();

    @TypeConverters({DateConverter.class})
    @Query("UPDATE substitutions_table SET status = :status WHERE uid = :substitutionId")
    void setStatus(int substitutionId, String status);

    @TypeConverters({DateConverter.class})
    @Query("DELETE FROM SUBSTITUTIONS_TABLE WHERE substitutionDate <= :deleteDate")
    void deleteBefore(Date deleteDate);
}
