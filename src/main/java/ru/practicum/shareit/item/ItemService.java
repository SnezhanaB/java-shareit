package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    ItemDto createItem(Integer userId, ItemDto item);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto item);

    ItemDto getItemById(int itemId);

    List<ItemDto> getAllItems(Integer userId);

    List<ItemDto> search(Integer userId, String query);

}
