package com.github.jukkarol.exception;

public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException() {
        super("Permission Denied.");
    }
}
