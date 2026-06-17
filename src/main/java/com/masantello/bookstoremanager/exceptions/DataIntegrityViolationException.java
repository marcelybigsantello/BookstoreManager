package com.masantello.bookstoremanager.exceptions;

import jakarta.persistence.PersistenceException;

public class DataIntegrityViolationException extends PersistenceException {

    public DataIntegrityViolationException(String msg) {
        super(msg);
    }
}
