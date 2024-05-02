package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public BookingDto createBooking(Integer userId, BookingCreateDto createDto) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        Item item = itemRepository.getItemById(createDto.getItemId());
        if (item == null) {
            throw new NotFoundException("Вещь с id=" + userId + " не найдена");
        }
        if (!item.isAvailable()) {
            throw new ValidationException("Вещь с id=" + userId + " не доступна к бронированию");
        }
        Integer ownerId = item.getOwner().getId();
        if (ownerId.equals(userId)) {
            throw new IllegalArgumentException("Вы не можете бронировать собственные вещи");
        }

        LocalDateTime now = LocalDateTime.now();
        if (createDto.getStart() == null || createDto.getEnd() == null) {
            throw new ValidationException("Дата начала и окончания должны быть заполнены");
        }
        if (createDto.getStart().isBefore(now)) {
            throw new ValidationException("Дата начала не может быть в прошлом");
        }
        if (createDto.getEnd().isBefore(now)) {
            throw new ValidationException("Дата окончания не может быть в прошлом");
        }
        if (createDto.getEnd().isBefore(createDto.getStart()) ||
                createDto.getEnd().equals(createDto.getStart())) {
            throw new ValidationException("Дата окончания не может быть раньше или равна дате начала");
        }

        Booking toCreate = mapper.map(createDto, Booking.class);
        toCreate.setId(null);
        toCreate.setStatus(BookingStatus.WAITING);
        toCreate.setItem(item);
        toCreate.setBooker(user);

        return mapper.map(bookingRepository.save(toCreate), BookingDto.class);
    }

    @Override
    public BookingDto updateStatus(Integer userId, Integer bookingId, Boolean approved) {

        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        Booking booking = bookingRepository.getBookingById(bookingId);
        if (booking == null) {
            throw new NotFoundException("Бронирование с id=" + userId + " не найдено");
        }

        Item item = itemRepository.getItemById(booking.getItem().getId());
        if (item == null) {
            throw new NotFoundException("Вещь с id=" + userId + " не найдена");
        }

        Integer itemOwnerId = item.getOwner().getId();
        Integer bookerId = booking.getBooker().getId();

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new DataValidationException("Подтверждать бронирование можно только в статусе 'Ожидает " +
                    "одобрения'");
        }

        if (userId.equals(itemOwnerId)) {
            // Если бронирование подтверждает владелец вещи
            booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        } else if (userId.equals(bookerId)) {
            // Если бронирование подтверждает пользователь, который осуществляет бронирование
            if (approved) {
                throw new IllegalArgumentException("Вы можете только отменить это бронирование!");
            }
            booking.setStatus(BookingStatus.CANCELED);
        } else {
            throw new DataValidationException("Вы не можете подтверждать это бронирование!");
        }

        return mapper.map(bookingRepository.save(booking), BookingDto.class);
    }

    @Override
    public BookingDto getBooking(Integer userId, Integer bookingId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        Booking booking = bookingRepository.getBookingById(bookingId);
        if (booking == null) {
            throw new NotFoundException("Бронирование с id=" + userId + " не найдено");
        }

        Item item = itemRepository.getItemById(booking.getItem().getId());
        if (item == null) {
            throw new NotFoundException("Вещь с id=" + userId + " не найдена");
        }

        boolean isItemOwner = item.getOwner().getId().equals(userId);
        boolean isBooker = booking.getBooker().getId().equals(userId);
        if (!isBooker && !isItemOwner) {
            throw new AccessViolationException("Вы не имеете доступ к этому бронированию!");
        }

        return mapper.map(booking, BookingDto.class);
    }

    @Override
    public List<BookingDto> getAllBookings(Integer userId, String state) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList;
        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(userId,
                        now, now);
                break;
            case "PAST":
                bookingList = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case "WAITING":
                bookingList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new DataValidationException("Unknown state: " + state);
        }

        return bookingList.stream().map((b) -> mapper.map(b, BookingDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(Integer ownerId, String state) {
        User owner = userRepository.getUserById(ownerId);
        if (owner == null) {
            throw new NotFoundException("Пользователь с id=" + ownerId + " не найден");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList;
        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                        now, now);
                break;
            case "PAST":
                bookingList = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
                break;
            case "WAITING":
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new DataValidationException("Unknown state: " + state);
        }

        return bookingList.stream().map((b) -> mapper.map(b, BookingDto.class)).collect(Collectors.toList());
    }

}
