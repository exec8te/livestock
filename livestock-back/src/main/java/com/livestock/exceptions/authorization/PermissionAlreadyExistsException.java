package com.livestock.exceptions.authorization;

public class PermissionAlreadyExistsException extends RuntimeException {
    public PermissionAlreadyExistsException(String message) {

        super("Permission already exists : " + message);
    }
}
