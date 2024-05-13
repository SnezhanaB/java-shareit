package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody BookingCreateDto createDto
    ) {
        log.info("createBooking, userId: {}, createDto: {}", userId, createDto);
        return service.createBooking(userId, createDto);
    }

    @PatchMapping("/{bookingId}")
    BookingDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer bookingId,
            @RequestParam() Boolean approved
    ) {
        log.info("approveBooking, userId: {}, bookingId: {}, approved: {}", userId, bookingId, approved);
        return service.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingDto getBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable Integer bookingId
    ) {
        log.info("getBooking, userId: {}, bookingId: {}", userId, bookingId);
        return service.getBooking(userId, bookingId);
    }

    @GetMapping()
    List<BookingDto> getBookings(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("getBookings, userId: {}, state: {}", userId, state);
        return service.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    List<BookingDto> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        log.info("getBookingsByOwner, userId: {}, state: {}", userId, state);
        return service.getAllBookingsByOwnerId(userId, state, from, size);
    }

}
