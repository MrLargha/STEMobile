package ru.mrlargha.stemobile.ui.main

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.mrlargha.stemobile.data.Result
import ru.mrlargha.stemobile.data.STERepository
import ru.mrlargha.stemobile.data.STERepository.Companion.getRepository
import ru.mrlargha.stemobile.data.model.SimpleServerReply
import ru.mrlargha.stemobile.data.model.Substitution
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val steRepository: STERepository? = getRepository(application.applicationContext)
    private val savedSubstitutions = ArrayList<Substitution>()

    val substitutionsList: LiveData<List<Substitution>>
    val syncProgress = MutableLiveData(-1)
    val undoString = MutableLiveData<String>()
    val errorString = MutableLiveData<String?>()
    val statusString = MutableLiveData<String>()

    fun deleteSubstitutions(ids: ArrayList<Long?>) {
        savedSubstitutions.clear()
        var canUndo = false
        var serverDel = 0
        if (substitutionsList.value != null) {
            for (substitution in substitutionsList.value!!) {
                if (ids.contains(substitution.iD.toLong())) {
                    if (substitution.status == Substitution.STATUS_NOT_SYNCHRONIZED) {
                        savedSubstitutions.add(substitution)
                        canUndo = true
                    } else {
                        serverDel++
                    }
                    DeleteTask().execute(substitution)
                }
            }
        }

        if (canUndo) {
            if (serverDel > 0) {
                // No sense to undo it
                statusString.setValue(String.format("Из локального хранилища удалено %s замещений." +
                        "С сервера удалено %s замещений",
                        savedSubstitutions.size, serverDel))
            } else {
                undoString.setValue(String.format("Из локального хранилища удалено %s замещений.",
                        savedSubstitutions.size))
            }
        } else {
            statusString.setValue(String.format("С сервера удалено %s замещений.", serverDel))
        }
    }

    fun undoLocalDeletion() {
        for (substitution in savedSubstitutions) {
            steRepository!!.insertSubstitutionToDB(substitution)
        }
        clearDeletionCache()
    }

    fun clearDeletionCache() {
        savedSubstitutions.clear()
        undoString.value = ""
    }

    fun syncSubstitutions(upload: Boolean) {
        FetchTask().execute(upload)
    }

    private inner class DeleteTask : AsyncTask<Substitution?, Void?, Void?>() {
        override fun doInBackground(vararg params: Substitution?): Void? {
            steRepository!!.deleteSubstitution(params[0]!!)
            return null
        }
    }

    fun forceSync() {
        steRepository!!.deleteAllLocalSubstitutions()
        FetchTask().execute(false)
    }

    // The firs parameter used as flag: true - upload local after sync, false - no upload
    private inner class FetchTask : AsyncTask<Boolean?, Int?, Boolean>() {
        override fun doInBackground(vararg params: Boolean?): Boolean? {
            try {
                publishProgress(0)
                val downloaded = steRepository!!.downloadUpdate()
                if (downloaded > 0) {
                    statusString.postValue("Загружено $downloaded замещений")
                }
            } catch (e: STERepository.SynchronizationException) {
                errorString.postValue(e.message)
                publishProgress(-1)
                return false
            }
            if (!params[0]!!) {
                publishProgress(-1)
            }
            return params[0]
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            syncProgress.value = values[0]
        }

        override fun onPostExecute(aBoolean: Boolean) {
            super.onPostExecute(aBoolean)
            if (aBoolean) {
                UploadTask().execute()
            }
        }
    }

    private inner class UploadTask : AsyncTask<Void?, Int?, Void?>() {
        override fun doInBackground(vararg params: Void?): Void? {
            var progress = 0
            publishProgress(progress)
            val substitutions = steRepository!!.unSyncSubstitutions
            if (substitutions.isEmpty()) {
                publishProgress(-1)
                return null
            }
            for (substitution in substitutions) {
                val result = steRepository.sendSubstitution(substitution)
                progress += 100 / substitutions.size
                publishProgress(progress)
                if (result !is Result.Success<*>) {
                    errorString.postValue((result as Result.Error).errorString)
                } else {
                    val success = result as Result.Success<SimpleServerReply?>
                    if (success.data!!.status == "error") {
                        steRepository.setSubstitutionStatus(substitution.iD,
                                Substitution.STATUS_ERROR)
                    } else {
                        steRepository.setSubstitutionStatus(substitution.iD,
                                Substitution.STATUS_SYNCHRONIZED)
                    }
                }
            }
            publishProgress(-1)
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            syncProgress.value = values[0]
        }
    }

    companion object {
        private const val TAG = "stemobile"
    }

    init {
        substitutionsList = steRepository!!.substitutions
        syncSubstitutions(false)
    }
}