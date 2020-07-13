package ru.mrlargha.stemobile.data.model

class SubstitutionsReply(status: String?, error_string: String?,
                         val substitutions: List<Substitution>
) : SimpleServerReply(status, error_string)