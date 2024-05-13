package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemRequestCreateDto {

    /**
     * Текст запроса, содержащий описание требуемой вещи
     */
    @NotBlank
    @NotNull
    private String description;

    /**
     * Пользователь, создавший запрос
     */
    private Integer requesterId;

}
