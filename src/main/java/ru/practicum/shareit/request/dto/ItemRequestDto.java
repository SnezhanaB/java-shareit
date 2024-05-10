package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemRequestDto {

    /**
     * Уникальный идентификатор запроса
     */
    private Integer id;

    /**
     * Текст запроса, содержащий описание требуемой вещи
     */
    private String description;

    /**
     * Дата и время создания запроса
     */
    private LocalDateTime created;

    /**
     * Список вещей
     */
    private List<ItemDto> items;
}
