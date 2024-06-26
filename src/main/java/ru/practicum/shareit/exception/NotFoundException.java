package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Data not found");
    }

    public NotFoundException(String msg) {
        super(msg);
    }
}
