package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;

/**
 * Вещь DTO
 */
@NoArgsConstructor
@Data
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

}
