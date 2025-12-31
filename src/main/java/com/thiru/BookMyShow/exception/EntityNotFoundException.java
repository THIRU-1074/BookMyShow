package com.thiru.BookMyShow.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> entityClass, Object id) {
        super(entityClass.getSimpleName() + " not found with id: " + id);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
