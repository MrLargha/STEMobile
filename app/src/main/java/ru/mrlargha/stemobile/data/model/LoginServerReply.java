package ru.mrlargha.stemobile.data.model;


public class LoginServerReply {

    private String status;
    private String name;
    private String permissions;
    private String ste_token;
    private String error_string;

    public LoginServerReply(String status, String name, String permissions, String ste_token, String error_string) {
        this.status = status;
        this.name = name;
        this.permissions = permissions;
        this.ste_token = ste_token;
        this.error_string = error_string;
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

    public String getStatus() {
        return status;
    }

    public String getError_string() {
        return error_string;
    }
}
