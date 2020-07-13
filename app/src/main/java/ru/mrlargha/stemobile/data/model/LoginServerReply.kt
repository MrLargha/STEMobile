package ru.mrlargha.stemobile.data.model

class LoginServerReply(
        status: String, val name: String, val permissions: String,
        val ste_token: String, error_string: String?
) : SimpleServerReply(status, error_string)