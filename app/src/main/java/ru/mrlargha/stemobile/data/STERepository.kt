package ru.mrlargha.stemobile.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import ru.mrlargha.stemobile.data.LoginRepository.Companion.getInstance
import ru.mrlargha.stemobile.data.model.Substitution
import ru.mrlargha.stemobile.data.model.SubstitutionsReply
import ru.mrlargha.stemobile.database.STEDao
import ru.mrlargha.stemobile.database.STERoomDatabase
import ru.mrlargha.stemobile.database.STERoomDatabase.Companion.getDatabase
import java.io.IOException
import java.util.*

class STERepository private constructor(context: Context) {
    private val steDao: STEDao?
    private val dataSource: STEDataSource
    fun insertSubstitutionToDB(substitution: Substitution?) {
        STERoomDatabase.databaseExecutor.execute { steDao!!.insertSubstitution(substitution) }
    }

    fun deleteSubstitution(substitution: Substitution) {
        STERoomDatabase.databaseExecutor.execute { steDao!!.deleteByUID(substitution.iD.toLong()) }
        // Delete from server if it is synchronized
        if (substitution.status == Substitution.STATUS_SYNCHRONIZED)
            dataSource.deleteSubstitution(getInstance(dataSource)!!.token, substitution)
    }

    val substitutions: LiveData<List<Substitution>>
        get() = steDao!!.allSubstitutions

    private val substitutionsSync: LinkedList<Substitution>
        get() = LinkedList(listOf(*steDao!!.allSubstitutionsSync))

    fun deleteAllLocalSubstitutions() {
        STERoomDatabase.databaseExecutor.execute { steDao!!.deleteAll() }
    }

    val unSyncSubstitutions: LinkedList<Substitution>
        get() = LinkedList(Arrays.asList(*steDao!!.unSyncSubstitutions))

    @Throws(SynchronizationException::class)
    fun downloadUpdate(): Int {
        val reference = Calendar.getInstance()
        reference[Calendar.HOUR_OF_DAY] = reference.getActualMinimum(Calendar.HOUR_OF_DAY)
        reference[Calendar.MINUTE] = reference.getActualMinimum(Calendar.MINUTE)
        reference[Calendar.SECOND] = reference.getActualMinimum(Calendar.SECOND)
        reference[Calendar.MILLISECOND] = reference.getActualMinimum(Calendar.MILLISECOND)
        val serverSubstitutionsReply = getSubstitutionsFromServer(reference.time.time.toInt())
        val localSubstitutions = substitutionsSync
        var insertedCounter = 0
        if (serverSubstitutionsReply is Result.Success<*>) {
            val serverSubstitutionsList = (serverSubstitutionsReply.data as SubstitutionsReply).substitutions
            for (substitution in localSubstitutions) {
                if (substitution.status == Substitution.STATUS_SYNCHRONIZED) {
                    var hasOnServer = false
                    for (serverSubstitution in serverSubstitutionsList) {
                        if (serverSubstitution.fullEquals(substitution)) {
                            hasOnServer = true
                            break
                        }
                    }
                    if (!hasOnServer) {
                        steDao!!.deleteByUID(substitution.iD.toLong())
                    }
                }
            }
            for (serverSubstitution in serverSubstitutionsList) {
                var hasOnClient = false
                for (localSubstitution in localSubstitutions) {
                    if (localSubstitution.fullEquals(serverSubstitution)) {
                        hasOnClient = true
                        break
                    }
                }
                if (!hasOnClient) {
                    serverSubstitution.status = Substitution.STATUS_SYNCHRONIZED
                    insertSubstitutionToDB(serverSubstitution)
                    insertedCounter++
                }
            }
        } else {
            throw SynchronizationException((serverSubstitutionsReply as Result.Error).errorString)
        }
        return insertedCounter
    }

    val formHints: Result<*>
        get() = dataSource.formHints

    fun setSubstitutionStatus(id: Int, status: String?) {
        steDao!!.setStatus(id, status)
    }

    fun sendSubstitution(substitution: Substitution?): Result<*> {
        return dataSource.sendSubstitution(substitution!!, getInstance(dataSource)!!.token)
    }

    private fun getSubstitutionsFromServer(date: Int): Result<*> {
        return dataSource.getSubstitutions(getInstance(dataSource)!!.token, date.toLong())
    }

    val users: Result<*>
        get() = dataSource.getUsers(getInstance(dataSource)!!.token)

    fun setUserPermission(user_id: Int, permission: String?): Result<*> {
        return dataSource.setPermission(getInstance(dataSource)!!.token,
                user_id, permission)
    }

    class SynchronizationException internal constructor(message: String?) : IOException(message)
    companion object {
        private const val TAG = "stemobile"
        private var INSTANCE: STERepository? = null

        @JvmStatic
        fun getRepository(context: Context): STERepository? {
            if (INSTANCE == null) {
                INSTANCE = STERepository(context)
            }
            return INSTANCE
        }
    }

    init {
        val steRoomDatabase = getDatabase(context)
        steDao = steRoomDatabase.substitutionDao()
        dataSource = STEDataSource()
        Log.d(TAG, "STERepository: created")
    }
}