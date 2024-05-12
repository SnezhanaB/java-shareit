package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.TestingUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService service;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = TestingUtils.createItemRequestDto();
    }

    @Test
    @SneakyThrows
    void create() {
        when(service.create(any()))
                .thenReturn(itemRequestDto);

        mvc.perform(
                        post("/requests")
                                .content(mapper.writeValueAsString(itemRequestDto))
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDto.getItems().get(0).getId())))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDto.getCreated().format(TestingUtils.DATE_TIME_FORMATTER))));
    }

    @Test
    @SneakyThrows
    void createNotValidI() {
        itemRequestDto.setDescription("");

        mvc.perform(
                        post("/requests")
                                .content(mapper.writeValueAsString(itemRequestDto))
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getById() {
        when(service.getById(eq(1), eq(1)))
                .thenReturn(itemRequestDto);

        mvc.perform(
                        get("/requests/1")
                                .content(mapper.writeValueAsString(itemRequestDto))
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDto.getItems().get(0).getId())))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDto.getCreated().format(TestingUtils.DATE_TIME_FORMATTER))));
    }

    @Test
    @SneakyThrows
    void getByRequester() {
        when(service.getByRequesterId(eq(1)))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(
                        get("/requests")
                                .content(mapper.writeValueAsString(itemRequestDto))
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].items[0].id", is(itemRequestDto.getItems().get(0).getId())))
                .andExpect(jsonPath("$.[0].created",
                        is(itemRequestDto.getCreated().format(TestingUtils.DATE_TIME_FORMATTER))));
    }

    @Test
    @SneakyThrows
    void getAll() {
        when(service.getRequests(eq(1), eq(0), eq(10)))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(
                        get("/requests/all")
                                .content(mapper.writeValueAsString(itemRequestDto))
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].items[0].id", is(itemRequestDto.getItems().get(0).getId())))
                .andExpect(jsonPath("$.[0].created",
                        is(itemRequestDto.getCreated().format(TestingUtils.DATE_TIME_FORMATTER))));
    }

    @Test
    @SneakyThrows
    void getAllWithQueryParams() {
        when(service.getRequests(eq(1), eq(20), eq(10)))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(
                        get("/requests/all")
                                .content(mapper.writeValueAsString(itemRequestDto))
                                .queryParam("from", "20")
                                .queryParam("size", "10")
                                .header(TestingUtils.X_USER_HEADER, 1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].items[0].id", is(itemRequestDto.getItems().get(0).getId())))
                .andExpect(jsonPath("$.[0].created",
                        is(itemRequestDto.getCreated().format(TestingUtils.DATE_TIME_FORMATTER))));
    }
}