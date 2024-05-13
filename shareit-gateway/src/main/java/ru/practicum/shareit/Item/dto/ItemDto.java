package ru.practicum.shareit.Item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * Вещь DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ItemDto {
    /**
     * Уникальный идентификатор вещи
     */
    private int id;
    /**
     * Краткое название
     */
    @NotBlank
    private String name;
    /**
     * Развёрнутое описание
     */
    @NotBlank
    private String description;
    /**
     * Статус о том, доступна или нет вещь для аренды
     */
    private Boolean available;
    /**
     * Идентификатор запроса
     */
    private Integer requestId;

}
