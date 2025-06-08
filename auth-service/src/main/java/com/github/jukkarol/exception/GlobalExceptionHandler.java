package com.github.jukkarol.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleBadCredentials(BadCredentialsException ex) {
        return Map.of(
                "error", ex.getMessage(),
                "description", "The username or password is incorrect"
        );
    }

    @ExceptionHandler(AccountStatusException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleAccountStatus(AccountStatusException ex) {
        return Map.of(
                "error", ex.getMessage(),
                "description", "The account is locked"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleAccessDenied(AccessDeniedException ex) {
        return Map.of(
                "error", ex.getMessage(),
                "description", "You are not authorized to access this resource"
        );
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleInvalidSignature(SignatureException ex) {
        return Map.of(
                "error", ex.getMessage(),
                "description", "The JWT signature is invalid"
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleExpiredJwt(ExpiredJwtException ex) {
        return Map.of(
                "error", ex.getMessage(),
                "description", "The JWT token has expired"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return errors;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnknownException(Exception ex) {
        return Map.of(
                "error", ex.getMessage(),
                "description", "Unknown internal server error"
        );
    }
}
