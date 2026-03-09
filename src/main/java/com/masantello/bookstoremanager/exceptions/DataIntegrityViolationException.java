package com.masantello.bookstoremanager.exceptions;

public class DataIntegrityViolationException extends RuntimeException {
    public DataIntegrityViolationException(String msg) {
        super(msg);
    }
}
