package ru.mrlargha.stemobile.ui.login

import ru.mrlargha.stemobile.data.model.LoginServerReply

internal class LoginResult {
    var success: LoginServerReply? = null
        private set
    var error: String? = null
        private set

    constructor(error: String?) {
        this.error = error
    }

    constructor(success: LoginServerReply?) {
        this.success = success
    }

}