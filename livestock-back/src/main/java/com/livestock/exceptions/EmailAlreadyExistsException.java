package com.livestock.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {

        super("Email already exists : " + message);
    }
}
