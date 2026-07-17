package com.livestock.exceptions.authorization;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(Long id) {
        super("Role not found with id: " + id);
    }

    public RoleNotFoundException(String message) {
        super("Role not found with with name: " + message);
    }
}
