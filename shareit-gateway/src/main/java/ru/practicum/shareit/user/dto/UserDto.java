package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Пользователь DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDto {
    /**
     * Уникальный идентификатор пользователя
     */
    private Long id;
    /**
     * Имя или логин пользователя
     */
    @NotNull
    @NotBlank
    private String name;
    /**
     * Адрес электронной почты
     */
    @NotNull
    @Email
    private String email;
}
