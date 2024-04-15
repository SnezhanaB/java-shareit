package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemInMemoryServiceImpl implements ItemService {

    private final UserService userService;

    private int currentId = 1;

    private final HashMap<Integer, Item> items = new HashMap<>();

    @Override
    public ItemDto createItem(Integer userId, ItemDto item) {
        UserDto owner = userService.getUserById(userId);

        if (item.getAvailable() == null) {
            throw new DataValidationException("Не заполнено поле 'available'");
        }

        Item created = ItemDto.toItemDto(item);
        created.setId(currentId++);
        created.setOwner(UserDto.toUser(owner));
        items.put(created.getId(), created);

        return Item.toItemDto(created);
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto item) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена");
        }
        Item updated = items.get(itemId);

        if (!userId.equals(updated.getOwner().getId())) {
            throw new AccessViolationException("Вы не можете обновлять чужую вещь");
        }

        if (item.getAvailable() != null) {
            updated.setAvailable(item.getAvailable());
        }

        if (item.getName() != null && !item.getName().isEmpty()) {
            updated.setName(item.getName());
        }

        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            updated.setDescription(item.getDescription());
        }

        items.put(itemId, updated);

        return Item.toItemDto(updated);
    }

    @Override
    public ItemDto getItemById(int itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена");
        }
        return Item.toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getAllItems(Integer userId) {
        return items.values().stream()
                .filter((i) -> i.getOwner().getId().equals(userId))
                .map(Item::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Integer userId, String query) {
        if (query.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter((i) -> ( i.isAvailable() && (
                        i.getName().toLowerCase().contains(query.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(query.toLowerCase())
                )))
                .map(Item::toItemDto).collect(Collectors.toList());
    }
}
