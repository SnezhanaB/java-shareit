package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Пользователь
 */
@Data
@NoArgsConstructor
public class User {
    /**
     * Уникальный идентификатор пользователя
     */
    private Integer id;
    /**
     * Имя или логин пользователя
     */
    private String name;
    /**
     * Адрес электронной почты
     */
    private String email;
}
