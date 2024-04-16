package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * Пользователь DTO
 */
@Data
@NoArgsConstructor
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
    private String email;
}
