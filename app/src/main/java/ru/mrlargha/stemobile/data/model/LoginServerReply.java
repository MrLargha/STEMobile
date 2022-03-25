package ru.mrlargha.stemobile.data.model;

/**
 * Модель ответа сервера
 */
public class LoginServerReply extends SimpleServerReply {

    private final String name;
    private final String permissions;
    private final String ste_token;

    /**
     * Конструктор по-умолчанию
     *
     * @param status       статус ответа
     * @param name         имя пользователя
     * @param permissions  права пользователя
     * @param ste_token    токен авторизации
     * @param error_string текст ошибки (если есть)
     */
    public LoginServerReply(String status, String name, String permissions, String ste_token, String error_string) {
        super(status, error_string);
        this.name = name;
        this.permissions = permissions;
        this.ste_token = ste_token;
    }

    public String getName() {
        return name;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getSte_token() {
        return ste_token;
    }
}
