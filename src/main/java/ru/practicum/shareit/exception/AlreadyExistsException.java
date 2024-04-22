package ru.practicum.shareit.exception;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException() {
        super("Already exists");
    }

    public AlreadyExistsException(String msg) {
        super(msg);
    }
}
