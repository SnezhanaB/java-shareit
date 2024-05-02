package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import java.time.LocalDateTime;

/**
 * Бронирование DTO
 */
@Data
@NoArgsConstructor
public class BookingCreateDto {
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
    @NonNull
    private Integer itemId;

}
