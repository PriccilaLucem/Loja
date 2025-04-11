package org.example.loja.enums;

public enum TokenType {
    ADMIN_MASTER,
    MANAGER,
    STORE_ADMIN;
    public static TokenType fromString(String value) {
        return switch (value) {
            case "AdminMaster" -> ADMIN_MASTER;
            case "Manager" -> MANAGER;
            case "StoreAdmin" -> STORE_ADMIN;
            default -> throw new RuntimeException("Invalid token type " + value);
        };
    }

}