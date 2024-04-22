package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import java.sql.Timestamp;

/**
 * Бронирование DTO
 */
@Data
@AllArgsConstructor
public class BookingDto {
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
    private int itemId;
    /**
     * Пользователь, который осуществляет бронирование
     */
    private int bookerId;
    /**
     * Статус бронирования
     */
    private BookingStatus status;

}
