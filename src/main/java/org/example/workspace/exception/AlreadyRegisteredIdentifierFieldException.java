package org.example.workspace.exception;

public class AlreadyRegisteredIdentifierFieldException extends MessageArgumentException {

    public AlreadyRegisteredIdentifierFieldException(String fieldName) {
        super(fieldName);
    }
}

