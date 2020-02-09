package ru.mrlargha.stemobile.data.model;

import java.util.List;

public class SubstitutionFormHints extends SimpleServerReply {
    private List<String> teachers;
    private List<String> subjects;
    private List<String> groups;

    SubstitutionFormHints(String status, String error_string, List<String> teachers,
                          List<String> subjects, List<String> groups) {
        super(status, error_string);
        this.teachers = teachers;
        this.subjects = subjects;
        this.groups = groups;
    }

    public List<String> getTeachers() {
        return teachers;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public List<String> getGroups() {
        return groups;
    }
}
