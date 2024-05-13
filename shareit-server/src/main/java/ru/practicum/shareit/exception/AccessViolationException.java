package ru.practicum.shareit.exception;

public class AccessViolationException extends RuntimeException {

    public AccessViolationException() {
        super("Access violation");
    }

    public AccessViolationException(String msg) {
        super(msg);
    }
}
