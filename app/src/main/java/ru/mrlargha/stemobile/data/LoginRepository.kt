package ru.mrlargha.stemobile.data

import ru.mrlargha.stemobile.data.model.LoginServerReply

class LoginRepository private constructor(private val dataSource: STEDataSource) {
    private var user: LoginServerReply? = null
    val isLoggedIn: Boolean
        get() = user!!.status == "ok"

    val token: String?
        get() = if (user != null) {
            user!!.ste_token
        } else {
            null
        }

    val name: String?
        get() = if (user != null) {
            user!!.name
        } else {
            null
        }

    fun logout() {
        dataSource.logout(user!!.ste_token)
        user = null
    }

    private fun setLoggedInUser(user: LoginServerReply) {
        this.user = user
    }

    fun login(username: String?, password: String?): Result<LoginServerReply> {
        val result = dataSource.login(username, password)
        if (result is Result.Success<*>) {
            setLoggedInUser((result as Result.Success<LoginServerReply>).data)
        }
        return result as Result<LoginServerReply>
    }

    fun register(vk_id: String?, password: String?): Result<LoginServerReply> {
        val result = dataSource.register(vk_id, password)
        if (result is Result.Success<*>) {
            setLoggedInUser((result as Result.Success<LoginServerReply>).data)
        }
        return result as Result<LoginServerReply>
    }

    fun getInfo(token: String?): Result<LoginServerReply> {
        val result = dataSource.getInfo(token)
        if (result is Result.Success<*>) {
            setLoggedInUser((result as Result.Success<LoginServerReply>).data)
        }
        return result as Result<LoginServerReply>
    }

    companion object {
        @Volatile
        private var instance: LoginRepository? = null

        @JvmStatic
        fun getInstance(dataSource: STEDataSource): LoginRepository? {
            if (instance == null) {
                instance = LoginRepository(dataSource)
            }
            return instance
        }
    }
}