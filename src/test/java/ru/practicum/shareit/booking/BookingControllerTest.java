package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.utils.TestingUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService service;

    private BookingDto bookingDto;

    private BookingCreateDto bookingCreateDto;


    @BeforeEach
    void setUp() {
        bookingDto = TestingUtils.createBookingDto();
        bookingCreateDto = TestingUtils.createBookingCreateDto();
    }

    @Test
    void createBooking() throws Exception {

        when(service.createBooking(anyInt(), any()))
            .thenReturn(bookingDto);

        mvc.perform(
            post("/bookings")
                .content(mapper.writeValueAsString(bookingCreateDto))
                .header(TestingUtils.X_USER_HEADER, 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
            .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(TestingUtils.DATE_TIME_FORMATTER))))
            .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(TestingUtils.DATE_TIME_FORMATTER))));
    }

    @Test
    @SneakyThrows
    void approveBooking() {
        when(service.updateStatus(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(
            patch("/bookings/1")
                .queryParam("approved", "true")
                .header(TestingUtils.X_USER_HEADER, 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
            .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(TestingUtils.DATE_TIME_FORMATTER))))
            .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(TestingUtils.DATE_TIME_FORMATTER))));
    }

    @Test
    @SneakyThrows
    void getBooking() {
        when(service.getBooking(anyInt(), anyInt()))
                .thenReturn(bookingDto);

        mvc.perform(
            get("/bookings/1")
                .header(TestingUtils.X_USER_HEADER, 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
            .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(TestingUtils.DATE_TIME_FORMATTER))))
            .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(TestingUtils.DATE_TIME_FORMATTER))));
    }

    @Test
    @SneakyThrows
    void getBookings() {
        when(service.getAllBookings(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings?from=0&size=10")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header(TestingUtils.X_USER_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(TestingUtils.DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(TestingUtils.DATE_TIME_FORMATTER))));
    }

    @Test
    @SneakyThrows
    void getBookingsByOwner() {
        when(service.getAllBookingsByOwnerId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner?from=0&size=10")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .header(TestingUtils.X_USER_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(TestingUtils.DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(TestingUtils.DATE_TIME_FORMATTER))));
    }
}