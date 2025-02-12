package com.example.tkproject.exception;

public class TransportationServiceException extends RuntimeException {
    public TransportationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}