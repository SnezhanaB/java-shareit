package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * Пользователь DTO
 */
@Data
@AllArgsConstructor
public class UserDto {
    /**
     * Уникальный идентификатор пользователя
     */
    private Integer id;
    /**
     * Имя или логин пользователя
     */
    @NotNull
    private String name;
    /**
     * Адрес электронной почты
     */
    @NotNull
    @Email
    private final String email;

    public static User toUser(UserDto user) {
        return new User(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
