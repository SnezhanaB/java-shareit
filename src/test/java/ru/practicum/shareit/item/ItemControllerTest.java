package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.utils.TestingUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService service;

    private ItemDto itemDto;
    private ItemExtendedDto itemExtendedDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = TestingUtils.createItemDto();
        itemExtendedDto = TestingUtils.createItemExtendedDto();
        commentDto = TestingUtils.createCommentDto();
    }

    @Test
    @SneakyThrows
    void createItem() {
        when(service.createItem(eq(1), any()))
                .thenReturn(itemDto);
        mvc.perform(
                    post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(TestingUtils.X_USER_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    @SneakyThrows
    void createNotValidItem() {
        itemDto.setName("");
        itemDto.setDescription("");

        mvc.perform(
                        post("/items")
                                .content(mapper.writeValueAsString(itemDto))
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateItem() {
        when(service.updateItem(eq(1), eq(1), any()))
                .thenReturn(itemDto);
        mvc.perform(
                        patch("/items/1")
                                .content(mapper.writeValueAsString(itemDto))
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    @SneakyThrows
    void getById() {
        when(service.getItemById(eq(1), eq(1)))
                .thenReturn(itemExtendedDto);
        mvc.perform(
                        get("/items/1")
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemExtendedDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemExtendedDto.getName())))
                .andExpect(jsonPath("$.description", is(itemExtendedDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemExtendedDto.getAvailable())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemExtendedDto.getNextBooking().getId())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemExtendedDto.getLastBooking().getId())))
                .andExpect(jsonPath("$.comments[0].id", is(itemExtendedDto.getComments().get(0).getId())));

    }

    @Test
    @SneakyThrows
    void getAll() {
        when(service.getAllItems(eq(1)))
                .thenReturn(List.of(itemExtendedDto));

        mvc.perform(
                        get("/items")
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemExtendedDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(itemExtendedDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemExtendedDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemExtendedDto.getAvailable())))
                .andExpect(jsonPath("$.[0].nextBooking.id", is(itemExtendedDto.getNextBooking().getId())))
                .andExpect(jsonPath("$.[0].lastBooking.id", is(itemExtendedDto.getLastBooking().getId())))
                .andExpect(jsonPath("$.[0].comments[0].id", is(itemExtendedDto.getComments().get(0).getId())));

    }

    @Test
    @SneakyThrows
    void getFilmsByQuery() {
        when(service.search(eq(1), eq("дрель")))
                .thenReturn(List.of(itemDto));

        mvc.perform(
                        get("/items/search")
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .queryParam("text", "дрель")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemExtendedDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(itemExtendedDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemExtendedDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemExtendedDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    void addComment() {
        when(service.addComment(eq(1), eq(1), any()))
                .thenReturn(commentDto);
        mvc.perform(
                        post("/items/1/comment")
                                .content(mapper.writeValueAsString(commentDto))
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(TestingUtils.START_AS_STRING)));
    }

    @Test
    @SneakyThrows
    void addNotValidComment() {
        commentDto.setText("");

        mvc.perform(
                        post("/items/1/comment")
                                .content(mapper.writeValueAsString(commentDto))
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }
}