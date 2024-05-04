package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemRequestCreateDto {

    /**
     * Уникальный идентификатор запроса
     */
    private Integer id;

    /**
     * Текст запроса, содержащий описание требуемой вещи
     */
    @NotNull
    private String description;

    /**
     * Пользователь, создавший запрос
     */
    private Integer requesterId;

    /**
     * Дата и время создания запроса
     */
    private LocalDateTime created;

    /**
     * Список вещей
     */
    private List<ItemDto> items;

}
