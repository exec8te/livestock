package com.livestock.exceptions;

import com.livestock.exceptions.authorization.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

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

            case RoleNotFoundException e -> {
                status = HttpStatus.NOT_FOUND;
                message = e.getMessage();
            }

            case PermissionNotFoundException e -> {
                status = HttpStatus.NOT_FOUND;
                message = e.getMessage();
            }

            case PermissionAlreadyExistsException e -> {
                status = HttpStatus.CONFLICT;
                message = e.getMessage();
            }

            case RoleAlreadyExistsException e -> {
                status = HttpStatus.CONFLICT;
                message = e.getMessage();
            }

            case EmailAlreadyExistsException e -> {
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

            case AccountStatusException e -> {
                status = HttpStatus.FORBIDDEN;
                message = "Account is locked or disabled";
            }

            case AccessDeniedException e -> {
                status = HttpStatus.FORBIDDEN;
                message = "You are not authorized to access this resource";
            }

            case SignatureException e -> {
                status = HttpStatus.UNAUTHORIZED;
                message = "JWT signature is invalid";
            }

            case ExpiredJwtException e -> {
                status = HttpStatus.UNAUTHORIZED;
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


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException exception
    ) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Validation failed");

        problemDetail.setProperty(
                "errors",
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .collect(Collectors.toMap(
                                error -> error.getField(),
                                error -> error.getDefaultMessage()
                        ))
        );

        problemDetail.setProperty(
                "timestamp",
                LocalDateTime.now()
        );

        return problemDetail;
    }
}