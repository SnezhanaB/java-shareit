package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.TestingUtils;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final ModelMapper mapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        // mocking userRepository
        User user1 = TestingUtils.createUser(1);
        User user2 = TestingUtils.createUser(2);
        when(userRepository.getUserById(eq(1)))
                .thenReturn(user1);
        when(userRepository.getUserById(eq(2)))
                .thenReturn(user2);
        when(userRepository.getUserById(eq(TestingUtils.INVALID_ID)))
                .thenReturn(null);

        // mocking itemRepository
        Item item1 = mapper.map(TestingUtils.createItemDto(1), Item.class);
        item1.setOwner(user1);
        // not available item
        Item item2 = mapper.map(TestingUtils.createItemDto(2), Item.class);
        item2.setOwner(user2);
        item2.setAvailable(false);
        // valid item for booking by user1
        Item item3 = mapper.map(TestingUtils.createItemDto(3), Item.class);
        item3.setOwner(user2);
        // item 1 by user 1
        when(itemRepository.getItemById(eq(1)))
                .thenReturn(item1);
        // not available item 2 by user 2
        when(itemRepository.getItemById(eq(2)))
                .thenReturn(item2);
        // item 3 by user 2
        when(itemRepository.getItemById(eq(3)))
                .thenReturn(item3);
        when(itemRepository.getItemById(eq(TestingUtils.INVALID_ID)))
                .thenReturn(null);
    }

    @Test
    void createBooking_withWrongUserId() {
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(TestingUtils.INVALID_ID, TestingUtils.createBookingCreateDto())
        );
        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void createBooking_withWrongItemId() {
        BookingCreateDto createDto = TestingUtils.createBookingCreateDto();
        createDto.setItemId(TestingUtils.INVALID_ID);
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(1, createDto)
        );
        assertEquals(e.getMessage(), "Вещь с id=" + TestingUtils.INVALID_ID + " не найдена");

    }

    @Test
    void createBooking_withNotAvailableItem() {
        BookingCreateDto createDto = TestingUtils.createBookingCreateDto();
        createDto.setItemId(2);
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, createDto)
        );
        assertEquals(e.getMessage(), "Вещь с id=" + createDto.getItemId() + " не доступна к бронированию");
    }

    @Test
    void createBooking_withOwnerItem() {
        BookingCreateDto createDto = TestingUtils.createBookingCreateDto();
        createDto.setItemId(1);
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(1, createDto)
        );
        assertEquals(e.getMessage(), "Вы не можете бронировать собственные вещи");
    }

    @Test
    void createBooking_noStart() {
        BookingCreateDto createDto = TestingUtils.createBookingCreateDto();
        createDto.setItemId(3);
        createDto.setStart(null);
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, createDto)
        );
        assertEquals(e.getMessage(), "Дата начала и окончания должны быть заполнены");
    }

    @Test
    void createBooking_noEnd() {
        BookingCreateDto createDto = TestingUtils.createBookingCreateDto();
        createDto.setItemId(3);
        createDto.setEnd(null);
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, createDto)
        );
        assertEquals(e.getMessage(), "Дата начала и окончания должны быть заполнены");
    }

    @Test
    void createBooking_startInThePast() {
        BookingCreateDto createDto = TestingUtils.createBookingCreateDto();
        createDto.setItemId(3);
        createDto.setStart(TestingUtils.now().minusDays(1));
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, createDto)
        );
        assertEquals(e.getMessage(), "Дата начала не может быть в прошлом");
    }

    @Test
    void createBooking_endInThePast() {
        BookingCreateDto createDto = TestingUtils.createBookingCreateDto();
        createDto.setItemId(3);
        createDto.setEnd(TestingUtils.now().minusDays(1));
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, createDto)
        );
        assertEquals(e.getMessage(), "Дата окончания не может быть в прошлом");
    }

    @Test
    void createBooking_success() {
        BookingCreateDto createDto = TestingUtils.createBookingCreateDto();
        createDto.setItemId(3);

        when(bookingRepository.save(any()))
                .thenReturn(mapper.map(createDto, Booking.class));

        BookingDto created = bookingService.createBooking(1, createDto);
        assertEquals(created.getStart(), createDto.getStart());
        assertEquals(created.getEnd(), createDto.getEnd());
        assertEquals(created.getItem().getId(), createDto.getItemId());

    }

    @Test
    void updateStatus() {
    }

    @Test
    void getBooking() {
    }

    @Test
    void getAllBookings() {
    }

    @Test
    void getAllBookingsByOwnerId() {
    }
}