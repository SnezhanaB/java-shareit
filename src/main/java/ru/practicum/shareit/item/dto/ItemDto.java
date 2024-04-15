package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;

/**
 * Вещь DTO
 */
@AllArgsConstructor
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
    /**
     * Владелец вещи
     */
    // private int ownerId;
    /**
     * Если вещь была создана по запросу другого пользователя, то в этом
     * поле будет храниться ссылка на соответствующий запрос
     */
    // private int requestId;

    public static Item toItemDto(ItemDto item) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }
}
