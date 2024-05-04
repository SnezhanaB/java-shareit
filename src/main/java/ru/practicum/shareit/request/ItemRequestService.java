package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestCreateDto createDto);

    ItemRequestDto getById(Integer userId, Integer requestId);

    List<ItemRequestDto> getByRequesterId(Integer userId);

    List<ItemRequestDto> getRequests(Integer userId, Integer from, Integer size);

}
