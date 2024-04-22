package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

@Transactional
public interface ItemService {
    @Transactional
    ItemDto createItem(Integer userId, ItemDto item);

    @Transactional
    ItemDto updateItem(Integer userId, Integer itemId, ItemDto item);

    @Transactional(readOnly = true)
    ItemDto getItemById(int itemId);

    @Transactional(readOnly = true)
    List<ItemDto> getAllItems(Integer userId);

    @Transactional(readOnly = true)
    List<ItemDto> search(Integer userId, String query);

}
