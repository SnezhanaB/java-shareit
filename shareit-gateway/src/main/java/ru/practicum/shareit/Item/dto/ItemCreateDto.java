package ru.practicum.shareit.Item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Вещь DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ItemCreateDto {
    /**
     * Уникальный идентификатор вещи
     */
    private int id;
    /**
     * Краткое название
     */
    @NotBlank
    @NotNull
    private String name;
    /**
     * Развёрнутое описание
     */
    @NotNull
    @NotBlank
    private String description;
    /**
     * Статус о том, доступна или нет вещь для аренды
     */
    @NotNull
    private Boolean available;
    /**
     * Идентификатор запроса
     */
    private Integer requestId;

}
