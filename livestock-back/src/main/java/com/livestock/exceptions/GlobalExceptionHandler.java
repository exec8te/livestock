package com.livestock.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception exception) {

        HttpStatus status;
        String message;

        switch (exception) {

            case UserNotFoundException e -> {
                status = HttpStatus.NOT_FOUND;
                message = e.getMessage();
            }

            case EmailAlreadyExistsException e -> {
                status = HttpStatus.CONFLICT;
                message = e.getMessage();
            }

            case RoleAlreadyExistsException e -> {
                status = HttpStatus.CONFLICT;
                message = e.getMessage();
            }

            case BadRequestException e -> {
                status = HttpStatus.BAD_REQUEST;
                message = e.getMessage();
            }

            case HttpMessageNotReadableException e -> {
                status = HttpStatus.BAD_REQUEST;
                message = "Invalid request body";
            }

            case BadCredentialsException e -> {
                status = HttpStatus.UNAUTHORIZED;
                message = "Username or password is incorrect";
            }

            case AccessDeniedException e -> {
                status = HttpStatus.FORBIDDEN;
                message = "You are not authorized to access this resource";
            }

            case SignatureException e -> {
                status = HttpStatus.FORBIDDEN;
                message = "JWT signature is invalid";
            }

            case ExpiredJwtException e -> {
                status = HttpStatus.FORBIDDEN;
                message = "JWT token has expired";
            }

            default -> {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "Internal server error";
            }
        }

        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        status,
                        message
                );

        problemDetail.setProperty(
                "timestamp",
                LocalDateTime.now()
        );

        return problemDetail;
    }
}