package ru.mrlargha.stemobile.data.model;

import java.util.List;

public class SubstitutionsReply extends SimpleServerReply {

    private List<Substitution> substitutions;

    SubstitutionsReply(String status, String error_string, List<Substitution> substitutions) {
        super(status, error_string);
        this.substitutions = substitutions;
    }

    public List<Substitution> getSubstitutions() {
        return substitutions;
    }
}
