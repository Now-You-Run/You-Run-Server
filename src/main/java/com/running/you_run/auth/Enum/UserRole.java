package com.running.you_run.auth.Enum;

public enum UserRole {
    USER("User"),
    ADMIN("Admin");

    private String name;

    UserRole(String name) {
        this.name = name;
    }
}
