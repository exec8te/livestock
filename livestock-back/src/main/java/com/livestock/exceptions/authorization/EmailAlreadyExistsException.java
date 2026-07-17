package com.livestock.exceptions.authorization;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {

        super("Email already exists : " + message);
    }
}
