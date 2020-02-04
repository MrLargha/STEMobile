package ru.mrlargha.stemobile.data.model;

public class SimpleServerReply {
    private String status;
    private String error_string;

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
