package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Статус бронирования
 */
@Getter
@AllArgsConstructor
public enum BookingStatus {
    /**
     * Новое бронирование, ожидает одобрения
     */
    WAITING("WAITING"),
    /**
     * Бронирование подтверждено владельцем
     */
    APPROVED("APPROVED"),
    /**
     * Бронирование отклонено владельцем
     */
    REJECTED("REJECTED"),
    /**
     * Бронирование отменено создателем
     */
    CANCELED("CANCELED");

    private final String value;
}
