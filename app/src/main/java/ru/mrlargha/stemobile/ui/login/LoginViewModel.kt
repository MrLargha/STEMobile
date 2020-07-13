package ru.mrlargha.stemobile.ui.login

import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mrlargha.stemobile.R
import ru.mrlargha.stemobile.data.LoginRepository.Companion.getInstance
import ru.mrlargha.stemobile.data.Result
import ru.mrlargha.stemobile.data.STEDataSource
import ru.mrlargha.stemobile.data.model.LoginServerReply
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginViewModel : ViewModel() {
    internal val loginFormState = MutableLiveData<LoginFormState>()
    internal val loginResult = MutableLiveData<LoginResult>()
    internal val loginRepository = getInstance(STEDataSource())

    private fun checkForm(username: String, password: String): Boolean {
        return if (!isUserNameValid(username)) {
            loginFormState.value = LoginFormState(R.string.invalid_username, null)
            false
        } else if (!isPasswordValid(password)) {
            loginFormState.value = LoginFormState(null, R.string.invalid_password)
            false
        } else {
            loginFormState.value = LoginFormState(true)
            true
        }
    }

    fun login(username: String, password: String) {
        if (checkForm(username, password)) {
            LoginTask().execute(username, hash(password))
        }
    }

    fun register(username: String, password: String) {
        if (checkForm(username, password)) {
            RegisterTask().execute(username, hash(password))
        }
    }

    fun getInfo(token: String?) {
        FetchInfoTask().execute(token)
    }

    fun loginDataChanged(username: String?, password: String?) {}
    private fun isUserNameValid(username: String): Boolean {
        return !username.isEmpty()
    }

    private fun isPasswordValid(password: String?): Boolean {
        return password != null && password.trim { it <= ' ' }.length > 5
    }

    private fun handleLoginResult(loggedInUserResult: Result<LoginServerReply>) {
        if (loggedInUserResult is Result.Success<*>) {
            val data = (loggedInUserResult as Result.Success<LoginServerReply?>).data
            loginResult.setValue(LoginResult(data))
        } else {
            loginResult.setValue(LoginResult((loggedInUserResult as Result.Error?)!!.errorString))
        }
    }

    fun logout() {
        LogoutTask().execute()
    }

    private inner class LoginTask : AuthTask() {
        override fun doInBackground(vararg params: String?): Result<LoginServerReply> {
            return loginRepository!!.login(params[0], params[1])
        }
    }

    private inner class LogoutTask : AsyncTask<Void?, Void?, Void?>() {
        override fun doInBackground(vararg params: Void?): Void? {
            loginRepository!!.logout()
            return null
        }
    }

    private inner class RegisterTask : AuthTask() {
        override fun doInBackground(vararg params: String?): Result<LoginServerReply> {
            return loginRepository!!.register(params[0], params[1])
        }
    }

    private inner class FetchInfoTask : AuthTask() {
        override fun doInBackground(vararg params: String?): Result<LoginServerReply> {
            return loginRepository!!.getInfo(params[0])
        }
    }

    private abstract inner class AuthTask : AsyncTask<String?, Void?, Result<LoginServerReply>>() {
        abstract override fun doInBackground(vararg params: String?): Result<LoginServerReply>

        override fun onPostExecute(loggedInUserResult: Result<LoginServerReply>) {
            handleLoginResult(loggedInUserResult)
        }
    }

    private fun hash(s: String): String {
        return try {
            val digest = MessageDigest.getInstance("sha256")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()
            val hexString = StringBuilder()
            for (b in messageDigest) hexString.append(Integer.toHexString(0xFF and b.toInt()))
            hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            ""
        }
    }
}