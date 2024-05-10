package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.ChunkRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public ItemRequestDto create(ItemRequestCreateDto createDto) {

        User requester = userRepository.findById(createDto.getRequesterId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + createDto.getRequesterId() + " не найден"));

        ItemRequest request = mapper.map(createDto, ItemRequest.class);

        request.setCreated(LocalDateTime.now());
        request.setRequester(requester);

        return mapper.map(requestRepository.save(request), ItemRequestDto.class);
    }

    @Override
    public ItemRequestDto getById(Integer userId, Integer requestId) {

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        ItemRequest founded = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + userId + " не найден"));

        ItemRequestDto requestDto = mapper.map(founded, ItemRequestDto.class);

        requestDto.setItems(itemRepository.getItemsByRequestId(requestId)
                .stream().map((i) -> mapper.map(i, ItemDto.class))
                .collect(Collectors.toList()));

        return requestDto;
    }

    @Override
    public List<ItemRequestDto> getByRequesterId(Integer userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return requestRepository
                .findAllByRequesterId(userId, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map((r) -> {
                    ItemRequestDto requestDto = mapper.map(r, ItemRequestDto.class);
                    requestDto.setItems(itemRepository.getItemsByRequestId(r.getId())
                            .stream().map((i) -> mapper.map(i, ItemDto.class))
                            .collect(Collectors.toList()));

                    return requestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getRequests(Integer userId, Integer from, Integer size) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Pageable page = new ChunkRequest(from, size, Sort.by(Sort.Direction.DESC, "created"));

        return requestRepository.findAllByRequesterIdNot(userId, page).stream()
                .map((r) -> {
                    ItemRequestDto requestDto = mapper.map(r, ItemRequestDto.class);
                    requestDto.setItems(itemRepository.getItemsByRequestId(r.getId())
                            .stream().map((i) -> mapper.map(i, ItemDto.class))
                            .collect(Collectors.toList()));

                    return requestDto;
                })
                .collect(Collectors.toList());
    }
}
