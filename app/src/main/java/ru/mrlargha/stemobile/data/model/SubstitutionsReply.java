package ru.mrlargha.stemobile.data.model;

import java.util.List;

/**
 * Модель ответа сервера со списком замещений
 */
public class SubstitutionsReply extends SimpleServerReply {

    private final List<Substitution> substitutions;

    /**
     * Конструктор по-умолчанию
     *
     * @param status        статус ответа
     * @param error_string  строка с текстом ошибки
     * @param substitutions список активных замещений
     */
    SubstitutionsReply(String status, String error_string, List<Substitution> substitutions) {
        super(status, error_string);
        this.substitutions = substitutions;
    }

    public List<Substitution> getSubstitutions() {
        return substitutions;
    }
}
