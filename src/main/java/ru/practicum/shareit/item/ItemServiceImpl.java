package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public ItemDto createItem(Integer userId, ItemDto itemDto) {
        User owner = userRepository.getUserById(userId);

        if (owner == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        if (itemDto.getAvailable() == null) {
            throw new DataValidationException("Не заполнено поле 'available'");
        }

        Item toCreate = mapper.map(itemDto, Item.class);
        toCreate.setOwner(owner);

        Item created = itemRepository.save(toCreate);

        return mapper.map(created, ItemDto.class);
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        Item updated = itemRepository.getItemById(itemId);

        if (updated == null) {
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена");
        }

        if (!userId.equals(updated.getOwner().getId())) {
            throw new AccessViolationException("Вы не можете обновлять чужую вещь");
        }

        if (itemDto.getAvailable() != null) {
            updated.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            updated.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            updated.setDescription(itemDto.getDescription());
        }

        return mapper.map(itemRepository.save(updated), ItemDto.class);
    }

    @Override
    public ItemDto getItemById(int itemId) {
        return mapper.map(itemRepository.getItemById(itemId), ItemDto.class);
    }

    @Override
    public List<ItemDto> getAllItems(Integer userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map((i) -> mapper.map(i, ItemDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Integer userId, String query) {
        if (query.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findAvailableItemsByNameOrDescription(query).stream()
                .map((i) -> mapper.map(i, ItemDto.class)).collect(Collectors.toList());
    }
}
