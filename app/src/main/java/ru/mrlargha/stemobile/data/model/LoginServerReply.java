package ru.mrlargha.stemobile.data.model;


public class LoginServerReply extends SimpleServerReply {

    private String name;
    private String permissions;
    private String ste_token;

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
