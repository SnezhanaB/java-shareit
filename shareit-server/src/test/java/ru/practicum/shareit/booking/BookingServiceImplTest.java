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
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.TestingUtils;

import javax.validation.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        // user without items
        User user3 = TestingUtils.createUser(3);
        when(userRepository.getUserById(eq(1)))
                .thenReturn(user1);
        when(userRepository.getUserById(eq(2)))
                .thenReturn(user2);
        when(userRepository.getUserById(eq(3)))
                .thenReturn(user3);
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
    void createBooking_endEqualsStart() {
        BookingCreateDto createDto = TestingUtils.createBookingCreateDto();
        createDto.setItemId(3);
        createDto.setEnd(createDto.getStart());
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(1, createDto)
        );
        assertEquals(e.getMessage(), "Дата окончания не может быть раньше или равна дате начала");
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
    void updateStatus_withWrongUserId() {
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(TestingUtils.INVALID_ID, 1, true)
        );
        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void updateStatus_withWrongBookingId() {
        when(bookingRepository.getBookingById(anyInt()))
                .thenReturn(null);

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(1, 1, true)
        );
        assertEquals(e.getMessage(), "Бронирование с id=" + 1 + " не найдено");
    }

    @Test
    void updateStatus_withWrongStatus() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);
        dto.setStatus(BookingStatus.REJECTED);

        when(bookingRepository.getBookingById(anyInt()))
                .thenReturn(mapper.map(dto, Booking.class));

        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> bookingService.updateStatus(1, 1, true)
        );
        assertEquals(e.getMessage(), "Подтверждать бронирование можно только в статусе 'Ожидает одобрения'");

    }

    @Test
    void updateStatus_withWrongUser() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.getBookingById(anyInt()))
                .thenReturn(mapper.map(dto, Booking.class));

        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> bookingService.updateStatus(3, 1, true)
        );
        assertEquals(e.getMessage(), "Вы не можете подтверждать это бронирование!");
    }

    @Test
    void updateStatus_byOwner() {
        BookingDto dto = TestingUtils.createBookingDto(1, 1, 1);

        when(bookingRepository.getBookingById(anyInt()))
                .thenReturn(mapper.map(dto, Booking.class));

        Booking saved = mapper.map(dto, Booking.class);
        saved.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.save(any()))
                .thenReturn(saved);

        BookingDto updatedDto = bookingService.updateStatus(1, 1, true);

        assertEquals(updatedDto.getStatus(), BookingStatus.APPROVED);

    }

    @Test
    void updateStatus_byBookerApprove() {
        BookingDto dto = TestingUtils.createBookingDto(1, 2, 1);

        when(bookingRepository.getBookingById(anyInt()))
                .thenReturn(mapper.map(dto, Booking.class));

        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.updateStatus(1, 1, true)
        );
        assertEquals(e.getMessage(), "Вы можете только отменить это бронирование!");
    }

    @Test
    void updateStatus_byBookerDisapprove() {
        BookingDto dto = TestingUtils.createBookingDto(1, 2, 1);

        when(bookingRepository.getBookingById(anyInt()))
                .thenReturn(mapper.map(dto, Booking.class));


        Booking saved = mapper.map(dto, Booking.class);
        saved.setStatus(BookingStatus.REJECTED);

        when(bookingRepository.save(any()))
                .thenReturn(saved);

        BookingDto updatedDto = bookingService.updateStatus(1, 1, false);

        assertEquals(updatedDto.getStatus(), BookingStatus.REJECTED);

    }

    @Test
    void getBooking_withWrongUserId() {
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(TestingUtils.INVALID_ID, 1)
        );
        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void getBooking_withWrongBookingId() {
        when(bookingRepository.getBookingById(anyInt()))
                .thenReturn(null);

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1, 1)
        );
        assertEquals(e.getMessage(), "Бронирование с id=" + 1 + " не найдено");
    }

    @Test
    void getBooking_withWrongItemId() {
        BookingDto dto = TestingUtils.createBookingDto(1, TestingUtils.INVALID_ID, 1);

        when(bookingRepository.getBookingById(anyInt()))
                .thenReturn(mapper.map(dto, Booking.class));

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1, 1)
        );
        assertEquals(e.getMessage(), "Вещь с id=" + TestingUtils.INVALID_ID + " не найдена");
    }

    @Test
    void getBooking_byNotOwnerOrBooker() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.getBookingById(anyInt()))
                .thenReturn(mapper.map(dto, Booking.class));

        AccessViolationException e = assertThrows(
                AccessViolationException.class,
                () -> bookingService.getBooking(3, 1)
        );
        assertEquals(e.getMessage(), "Вы не имеете доступ к этому бронированию!");
    }

    @Test
    void getBooking_success() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.getBookingById(anyInt()))
                .thenReturn(mapper.map(dto, Booking.class));

        BookingDto result = bookingService.getBooking(1, 1);
        assertNotNull(result);
    }

    @Test
    void getAllBookings_withWrongUserId() {
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllBookings(TestingUtils.INVALID_ID, "ALL",0, 10)
        );
        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void getAllBookings_withWrongFrom() {
        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> bookingService.getAllBookings(1, "ALL",-1, 10)
        );
        assertEquals(e.getMessage(), "Offset must not be less than zero!");
    }

    @Test
    void getAllBookings_withWrongSize() {
        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> bookingService.getAllBookings(1, "ALL",0, 0)
        );
        assertEquals(e.getMessage(), "Limit must not be less than 1!");
    }

    @Test
    void getAllBookings_withWrongState() {
        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> bookingService.getAllBookings(1, "WRONG",0, 10)
        );
        assertEquals(e.getMessage(), "Unknown state: WRONG");
    }

    @Test
    void getAllBookings_withStateALL() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByBookerId(anyInt(), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookings(1, "ALL",0, 10);
        assertEquals(result.size(), 1);
    }

    @Test
    void getAllBookings_withStateCURRENT() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(anyInt(), any(), any(), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookings(1, "CURRENT",0, 10);
        assertEquals(result.size(), 1);
    }

    @Test
    void getAllBookings_withStatePAST() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByBookerIdAndEndBefore(anyInt(), any(), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookings(1, "PAST",0, 10);
        assertEquals(result.size(), 1);
    }

    @Test
    void getAllBookings_withStateFUTURE() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByBookerIdAndStartAfter(anyInt(), any(), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookings(1, "FUTURE",0, 10);
        assertEquals(result.size(), 1);
    }

    @Test
    void getAllBookings_withStateWAITING() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByBookerIdAndStatus(anyInt(), eq(BookingStatus.WAITING), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookings(1, "WAITING",0, 10);
        assertEquals(result.size(), 1);
    }

    @Test
    void getAllBookings_withStateREJECTED() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByBookerIdAndStatus(anyInt(), eq(BookingStatus.REJECTED), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookings(1, "REJECTED",0, 10);
        assertEquals(result.size(), 1);
    }

    @Test
    void getAllBookingsByOwnerId_withWrongUserId() {
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllBookingsByOwnerId(TestingUtils.INVALID_ID, "ALL",0, 10)
        );
        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void getAllBookingsByOwnerId_withWrongFrom() {
        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> bookingService.getAllBookingsByOwnerId(1, "ALL",-1, 10)
        );
        assertEquals(e.getMessage(), "Offset must not be less than zero!");
    }

    @Test
    void getAllBookingsByOwnerId_withWrongSize() {
        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> bookingService.getAllBookingsByOwnerId(1, "ALL",0, 0)
        );
        assertEquals(e.getMessage(), "Limit must not be less than 1!");
    }

    @Test
    void getAllBookingsByOwnerId_withWrongState() {
        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> bookingService.getAllBookingsByOwnerId(1, "WRONG",0, 10)
        );
        assertEquals(e.getMessage(), "Unknown state: WRONG");
    }

    @Test
    void getAllBookingsByOwnerId_withStateALL() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByItemOwnerId(anyInt(), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(1, "ALL",0, 10);
        assertEquals(result.size(), 1);
    }

    @Test
    void getAllBookingsByOwnerId_withStateCURRENT() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(anyInt(), any(), any(), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(1, "CURRENT",0, 10);
        assertEquals(result.size(), 1);
    }

    @Test
    void getAllBookingsByOwnerId_withStatePAST() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(anyInt(), any(), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(1, "PAST",0, 10);
        assertEquals(result.size(), 1);
    }

    @Test
    void getAllBookingsByOwnerId_withStateFUTURE() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(anyInt(), any(), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(1, "FUTURE",0, 10);
        assertEquals(result.size(), 1);
    }

    @Test
    void getAllBookingsByOwnerId_withStateWAITING() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyInt(), eq(BookingStatus.WAITING), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(1, "WAITING",0, 10);
        assertEquals(result.size(), 1);
    }

    @Test
    void getAllBookingsByOwnerId_withStateREJECTED() {
        BookingDto dto = TestingUtils.createBookingDto(1, 3, 1);

        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyInt(), eq(BookingStatus.REJECTED), any()))
                .thenReturn(List.of(mapper.map(dto, Booking.class)));

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(1, "REJECTED",0, 10);
        assertEquals(result.size(), 1);
    }
}