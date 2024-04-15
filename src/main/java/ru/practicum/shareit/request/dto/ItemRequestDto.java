package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    /**
     * Уникальный идентификатор запроса
     */
    private int id;
    /**
     * Текст запроса, содержащий описание требуемой вещи
     */
    private String description;
    /**
     * Пользователь, создавший запрос
     */
    private int requesterId;
    /**
     * Дата и время создания запроса
     */
    private Timestamp created;
}
