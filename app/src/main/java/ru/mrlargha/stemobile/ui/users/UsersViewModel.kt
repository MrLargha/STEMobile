package ru.mrlargha.stemobile.ui.users

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.mrlargha.stemobile.data.Result
import ru.mrlargha.stemobile.data.STERepository
import ru.mrlargha.stemobile.data.STERepository.Companion.getRepository
import ru.mrlargha.stemobile.data.model.SimpleServerReply
import ru.mrlargha.stemobile.data.model.User
import ru.mrlargha.stemobile.data.model.UsersReply
import java.util.*

class UsersViewModel(application: Application) : AndroidViewModel(application) {
    val usersLiveData = MutableLiveData<List<User>>(LinkedList())
    val hasNetworkOperationInProgress = MutableLiveData(false)
    val errorString = MutableLiveData("")
    private val mSTERepository: STERepository?
    fun setUserPermission(user: User) {
        hasNetworkOperationInProgress.value = true
        SetUserPermissionTask().execute(user.vk_id.toString(), user.permissions)
    }

    private inner class GetUsersTask : AsyncTask<Void?, Void?, List<User>>() {
        override fun doInBackground(vararg params: Void?): List<User>? {
            val result: Result<*> = mSTERepository!!.users
            return if (result is Result.Success<*>) {
                (result as Result.Success<UsersReply?>).data!!.users
            } else {
                errorString.postValue((result as Result.Error).errorString)
                LinkedList()
            }
        }

        override fun onPostExecute(users: List<User>) {
            super.onPostExecute(users)
            if (!users.isEmpty()) {
                usersLiveData.value = users
            }
            hasNetworkOperationInProgress.value = false
        }
    }

    private inner class SetUserPermissionTask : AsyncTask<String?, Void?, String?>() {
        override fun doInBackground(vararg params: String?): String? {
            val result: Result<*> = mSTERepository!!.setUserPermission(params[0]!!.toInt(), params[1])
            return if (result is Result.Success<*>) {
                (result as Result.Success<SimpleServerReply?>).data!!.status
            } else {
                (result as Result.Error).errorString
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == "ok") {
                hasNetworkOperationInProgress.setValue(false)
            } else {
                errorString.postValue(result)
                GetUsersTask().execute()
            }
        }
    }

    init {
        mSTERepository = getRepository(application)
        hasNetworkOperationInProgress.value = true
        GetUsersTask().execute()
    }
}