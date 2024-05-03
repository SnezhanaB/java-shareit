package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import java.time.LocalDateTime;

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
    private LocalDateTime start;

    /**
     * Дата и время конца бронирования
     */
    private LocalDateTime end;

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
