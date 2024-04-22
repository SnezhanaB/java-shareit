package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

/**
 * Вещь
 */
@Data
@NoArgsConstructor
public class Item {
    /**
     * Уникальный идентификатор вещи
     */
    private int id;
    /**
     * Краткое название
     */
    private String name;
    /**
     * Развёрнутое описание
     */
    private String description;
    /**
     * Статус о том, доступна или нет вещь для аренды
     */
    private boolean available;
    /**
     * Владелец вещи
     */
    private User owner;

    public Item(int id, String name, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
