package ru.mrlargha.stemobile.data.model

class UsersReply(status: String?, error_string: String?, val users: List<User>
) : SimpleServerReply(status, error_string)