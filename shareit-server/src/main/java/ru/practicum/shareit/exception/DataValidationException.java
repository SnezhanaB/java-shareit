package ru.practicum.shareit.exception;

public class DataValidationException extends RuntimeException {

    public DataValidationException() {
        super("Invalid data");
    }

    public DataValidationException(String msg) {
        super(msg);
    }
}
