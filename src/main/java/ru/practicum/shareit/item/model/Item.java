package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * Вещь
 */
@Entity
@NoArgsConstructor
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
public class Item {
    /**
     * Уникальный идентификатор вещи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * Краткое название
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    /**
     * Развёрнутое описание
     */
    @Column(name = "description", nullable = false)
    private String description;
    /**
     * Статус о том, доступна или нет вещь для аренды
     */
    @Column(name = "available", nullable = false)
    private boolean available;
    /**
     * Владелец вещи
     */
    @ManyToOne(fetch = FetchType.LAZY)
    // исключаем все поля с отложенной загрузкой из
    // метода toString, чтобы не было случайных обращений
    // базе данных, например при выводе в лог.
    @ToString.Exclude
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private ItemRequest request;
}
