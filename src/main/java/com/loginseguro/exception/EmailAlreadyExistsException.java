package com.loginseguro.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {

        super("Este email já está cadastrado");
    }
}
