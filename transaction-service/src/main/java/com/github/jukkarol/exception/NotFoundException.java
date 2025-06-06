package com.github.jukkarol.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String resource;
    private final String id;

    public NotFoundException(String resource, String id) {
        super("Resource not found.");
        this.resource = resource;
        this.id = id;
    }

}
