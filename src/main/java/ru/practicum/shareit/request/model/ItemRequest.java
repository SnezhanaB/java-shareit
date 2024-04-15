package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;

/**
 * Запрос вещи
 */
@Data
public class ItemRequest {
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
    private User requester;
    /**
     * Дата и время создания запроса
     */
    private Timestamp created;

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequester().getId(),
                request.getCreated()
        );
    }
}