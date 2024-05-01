package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Timestamp;

/**
 * Бронирование DTO
 */
@Data
@NoArgsConstructor
public class BookingCreateDto {
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
    @NonNull
    private Integer itemId;

}
