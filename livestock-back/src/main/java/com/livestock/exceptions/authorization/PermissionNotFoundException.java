package com.livestock.exceptions.authorization;

public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException(Long id) {
        super("Permission not found with id: " + id);
    }
}
