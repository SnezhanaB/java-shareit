package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TestingUtils {
    private TestingUtils() {
    }

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static final String START_AS_STRING = "2024-05-01T20:00:00";

    public static final String END_AS_STRING = "2024-05-01T22:00:00";

    public static final LocalDateTime START_DATE = LocalDateTime.parse(START_AS_STRING, DATE_TIME_FORMATTER);

    public static final LocalDateTime END_DATE = LocalDateTime.parse(END_AS_STRING, DATE_TIME_FORMATTER);

    public static UserDto createUserDto() {
        return new UserDto(1, "User", "user@example.com");
    }

    public static ItemDto createItemDto() {
      return new ItemDto(1, "Дрель", "Аккумуляторная", true, 1);
    }

    public static BookingDto createBookingDto() {
        return new BookingDto(1, START_DATE, END_DATE, createItemDto(), createUserDto(), BookingStatus.WAITING);
    }

    public static BookingCreateDto createBookingCreateDto() {
        return new BookingCreateDto(START_DATE, END_DATE, 1);
    }

}
