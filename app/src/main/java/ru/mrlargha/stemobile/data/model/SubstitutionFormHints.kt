package ru.mrlargha.stemobile.data.model

class SubstitutionFormHints(status: String, error_string: String?, val teachers: List<String>,
                            val subjects: List<String>, val groups: List<String>
) : SimpleServerReply(status, error_string)