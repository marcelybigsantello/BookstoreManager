package com.masantello.bookstoremanager.exceptions;

public class MissingMandatoryFieldsException extends RuntimeException {

    public MissingMandatoryFieldsException(String message) { super(message); }
}
