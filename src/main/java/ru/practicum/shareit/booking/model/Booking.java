package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;

/**
 * Бронирование
 */
@Data
public class Booking {
    /**
     * Уникальный идентификатор бронирования
     */
    private int id;
    /**
     * Дата и время начала бронирования
     */
    private Timestamp start;
    /**
     * Дата и время конца бронирования
     */
    private Timestamp end;
    /**
     * Вещь, которую пользователь бронирует
     */
    private Item item;
    /**
     * Пользователь, который осуществляет бронирование
     */
    private User booker;
    /**
     * Статус бронирования
     */
    private BookingStatus status;

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

}
