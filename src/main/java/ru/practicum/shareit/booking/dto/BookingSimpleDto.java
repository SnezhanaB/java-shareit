package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import java.sql.Timestamp;

/**
 * Бронирование DTO
 */
@Data
@NoArgsConstructor
public class BookingSimpleDto {

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
