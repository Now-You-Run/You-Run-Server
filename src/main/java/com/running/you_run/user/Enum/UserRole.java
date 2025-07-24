package com.running.you_run.user.Enum;

public enum UserRole {
    USER("User"),
    ADMIN("Admin");

    private String name;

    UserRole(String name) {
        this.name = name;
    }
}
