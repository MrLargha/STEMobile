package ru.mrlargha.stemobile.data.model;

import java.util.List;

public class UsersReply extends SimpleServerReply {

    private List<User> users;

    public UsersReply(String status, String error_string, List<User> userList) {
        super(status, error_string);
        users = userList;
    }

    public List<User> getUsers() {
        return users;
    }
}
