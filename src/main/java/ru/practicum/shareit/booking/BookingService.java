package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Transactional
public interface BookingService {

    @Transactional
    BookingDto createBooking(Integer userId, BookingCreateDto createDto);

    @Transactional
    BookingDto updateStatus(Integer userId, Integer bookingId, Boolean approved);

    BookingDto getBooking(Integer userId, Integer bookingId);

    List<BookingDto> getAllBookings(Integer userId, String state);

    List<BookingDto> getAllBookingsByOwnerId(Integer ownerId, String state);

}
