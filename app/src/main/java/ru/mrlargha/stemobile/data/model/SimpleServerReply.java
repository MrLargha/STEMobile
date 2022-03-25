package ru.mrlargha.stemobile.data.model;

/**
 * Модель ответа от сервера
 */
public class SimpleServerReply {
    private final String status;
    private final String error_string;

    /**
     * Конструктор по-умолчанию
     *
     * @param status       стутус ответа
     * @param error_string текст ошибки при ее наличии
     */
    SimpleServerReply(String status, String error_string) {
        this.status = status;
        this.error_string = error_string;
    }

    public String getStatus() {
        return status;
    }

    public String getError_string() {
        return error_string;
    }
}
