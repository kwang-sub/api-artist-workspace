package org.example.workspace.exception;

public class EntityNotFoundException extends MessageArgumentException {

    public EntityNotFoundException(Class<?> entityClass, Long id) {
        super(entityClass.getSimpleName(), id);
    }
}

