package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * Пользователь
 */
@Data
@AllArgsConstructor
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

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
