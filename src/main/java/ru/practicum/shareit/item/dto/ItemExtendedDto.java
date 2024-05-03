package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@Data
public class ItemExtendedDto {

    /**
     * Уникальный идентификатор вещи
     */
    private int id;

    /**
     * Краткое название
     */
    @NotBlank
    private String name;

    /**
     * Развёрнутое описание
     */
    @NotBlank
    private String description;

    /**
     * Статус о том, доступна или нет вещь для аренды
     */
    private Boolean available;

    /**
     * Предыдущее бронирование
     */
    private BookingSimpleDto lastBooking;

    /**
     * Следующее бронирование
     */
    private BookingSimpleDto nextBooking;

    /**
     * Комментарий
     */
    private List<CommentDto> comments;
}
