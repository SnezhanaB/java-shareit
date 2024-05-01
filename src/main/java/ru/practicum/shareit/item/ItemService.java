package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;

import java.util.List;

@Transactional
public interface ItemService {
    @Transactional
    ItemDto createItem(Integer userId, ItemDto item);

    @Transactional
    ItemDto updateItem(Integer userId, Integer itemId, ItemDto item);

    @Transactional(readOnly = true)
    ItemExtendedDto getItemById(Integer userId, Integer itemId);

    @Transactional(readOnly = true)
    List<ItemExtendedDto> getAllItems(Integer userId);

    @Transactional(readOnly = true)
    List<ItemDto> search(Integer userId, String query);

    @Transactional
    CommentDto addComment(Integer itemId, Integer userId, CommentCreateDto commentCreateDto);

}
