package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;


/**
 * Бронирование DTO
 */
@Data
@NoArgsConstructor
public class BookingDto {
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
    private ItemDto item;
    /**
     * Пользователь, который осуществляет бронирование
     */
    private UserDto booker;
    /**
     * Статус бронирования
     */
    private BookingStatus status;

}
