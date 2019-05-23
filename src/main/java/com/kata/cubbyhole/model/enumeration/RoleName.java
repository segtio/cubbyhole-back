package com.kata.cubbyhole.model.enumeration;

public enum RoleName {
    ROLE_USER,
    ROLE_ADMIN;

    public static RoleName getRoleName(String value) {
        try {
            return RoleName.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
