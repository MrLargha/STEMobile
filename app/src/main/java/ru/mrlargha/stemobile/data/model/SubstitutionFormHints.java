package ru.mrlargha.stemobile.data.model;

import java.util.List;

/**
 * Модель подсказок для заполнения формы нового замещения
 */
public class SubstitutionFormHints extends SimpleServerReply {
    private final List<String> teachers;
    private final List<String> subjects;
    private final List<String> groups;

    /**
     * Конструктор по-умолчканию
     *
     * @param status       статус ответа сервера
     * @param error_string строка с текстом ошибки
     * @param teachers     список преподавателей
     * @param subjects     список предметов
     * @param groups       списко учебных групп
     */
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
