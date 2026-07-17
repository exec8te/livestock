package com.livestock.exceptions.authorization;

public class RoleAlreadyExistsException extends RuntimeException {
    public RoleAlreadyExistsException(String message) {

        super("Role already exists : " + message);
    }
}
