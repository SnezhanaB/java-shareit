package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.Item.ItemController;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.user.UserController;
import java.util.Map;

@RestControllerAdvice(assignableTypes = {
        UserController.class,
        ItemController.class,
        BookingController.class
})
public class ErrorHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(final IllegalArgumentException e) {
        return Map.of("error", e.getMessage());
    }

}
