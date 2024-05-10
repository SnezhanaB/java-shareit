package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(
        @RequestHeader("X-Sharer-User-Id") Integer userId,
        @Valid @RequestBody ItemRequestCreateDto createDto
    ) {
        createDto.setRequesterId(userId);
        return requestService.create(createDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(
        @RequestHeader("X-Sharer-User-Id") Integer userId,
        @PathVariable("requestId") Integer requestId
    ) {
        return requestService.getById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getByRequester(
        @RequestHeader("X-Sharer-User-Id") Integer userId
    ) {
        return requestService.getByRequesterId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(
        @RequestHeader("X-Sharer-User-Id") Integer userId,
        @RequestParam(required = false, defaultValue = "0") Integer from,
        @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return requestService.getRequests(userId, from, size);
    }
}
