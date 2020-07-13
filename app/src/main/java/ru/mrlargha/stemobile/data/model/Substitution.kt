package ru.mrlargha.stemobile.data.model

import androidx.room.*
import com.google.gson.annotations.Expose
import ru.mrlargha.stemobile.database.DateConverter
import java.util.*

@Entity(tableName = "substitutions_table", indices = [Index(value = ["group", "substitutionDate", "pair"], unique = true)])
class Substitution(@field:Expose val teacher: String, @field:Expose val subject: String,
                   @field:Expose val group: Int, @field:Expose val pair: Int,
                   @field:Expose @field:TypeConverters(DateConverter::class) val substitutionDate: Date,
                   @field:Expose val cabinet: String, var status: String, val author: String) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var iD = 0

    fun equals(comparable: Substitution): Boolean {
        return comparable.pair == pair && comparable.substitutionDate == substitutionDate && comparable.group == group
    }

    fun fullEquals(comparable: Substitution): Boolean {
        return comparable.pair == pair && comparable.substitutionDate == substitutionDate && comparable.group == group && teacher == comparable.teacher && subject == comparable.subject && cabinet == comparable.cabinet && author == comparable.author
    }

    companion object {
        const val STATUS_NOT_SYNCHRONIZED = "NOT_SYNCHRONIZED"
        const val STATUS_SYNCHRONIZED = "SYNCHRONIZED"
        const val STATUS_ERROR = "ERROR"
    }

}