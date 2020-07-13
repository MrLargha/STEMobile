package ru.mrlargha.stemobile.ui.login

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    var usernameError: Int?
    var passwordError: Int?
    var isDataValid: Boolean

    constructor(usernameError: Int?, passwordError: Int?) {
        this.usernameError = usernameError
        this.passwordError = passwordError
        isDataValid = false
    }

    constructor(isDataValid: Boolean) {
        usernameError = null
        passwordError = null
        this.isDataValid = isDataValid
    }

}