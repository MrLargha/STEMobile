package ru.mrlargha.stemobile.data.model;

import java.util.List;

/**
 * Ответ от сервера со списком пользователей
 */
public class UsersReply extends SimpleServerReply {

    private final List<User> users;

    /**
     * Конструктор по-умолчанию
     *
     * @param status       статус ответа
     * @param error_string текст ошибки
     * @param userList     список пользователей
     */
    public UsersReply(String status, String error_string, List<User> userList) {
        super(status, error_string);
        users = userList;
    }

    public List<User> getUsers() {
        return users;
    }
}
