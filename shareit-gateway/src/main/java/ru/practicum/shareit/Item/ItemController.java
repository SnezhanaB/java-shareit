package ru.practicum.shareit.Item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Item.dto.CommentCreateDto;
import ru.practicum.shareit.Item.dto.ItemCreateDto;
import ru.practicum.shareit.Item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    ResponseEntity<Object> createItem(
            @RequestHeader(USER_ID_HEADER) @NotNull Integer userId,
            @Valid @RequestBody ItemCreateDto item) {
        return itemClient.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(USER_ID_HEADER) @NotNull Integer userId,
            @PathVariable @NotNull Integer itemId,
            @RequestBody @NotNull ItemDto item
    ) {
        return itemClient.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(USER_ID_HEADER) @NotNull Integer userId,
            @PathVariable @NotNull Integer itemId
    ) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER) Integer userId) {
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getFilmsByQuery(
            @RequestHeader(USER_ID_HEADER) @NotNull Integer userId,
            @RequestParam(name = "text", defaultValue = "") String text
    ) {
        if (text.isBlank()) {
            List<Object> empty = Collections.emptyList();
            return ResponseEntity.of(java.util.Optional.of(empty));
        }
        return itemClient.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(USER_ID_HEADER) @NotNull Integer userId,
            @PathVariable @NotNull Integer itemId,
            @RequestBody @Valid CommentCreateDto commentCreateDto
    ) {
        return itemClient.addComment(itemId, userId, commentCreateDto);
    }
}
