package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.AccessViolationException;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.TestingUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl service;

    private final ModelMapper mapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        // mocking userRepository
        User user1 = TestingUtils.createUser(1);
        User user2 = TestingUtils.createUser(2);
        User user3 = TestingUtils.createUser(3);
        when(userRepository.getUserById(eq(1)))
                .thenReturn(user1);
        when(userRepository.getUserById(eq(2)))
                .thenReturn(user2);
        when(userRepository.getUserById(eq(3)))
                .thenReturn(user3);
        when(userRepository.getUserById(eq(TestingUtils.INVALID_ID)))
                .thenReturn(null);
    }

    @Test
    void createItem_withNullAvailable() {
        ItemDto dto = TestingUtils.createItemDto();
        dto.setAvailable(null);
        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> service.createItem(1, dto)
        );
        assertEquals(e.getMessage(), "Не заполнено поле 'available'");
    }

    @Test
    void createItem_withWrongUserId() {
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.createItem(TestingUtils.INVALID_ID, TestingUtils.createItemDto())
        );
        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void createItem_success() {
        ItemDto dto = TestingUtils.createItemDto();
        Item created = mapper.map(dto, Item.class);
        created.setOwner(TestingUtils.createUser());
        when(itemRepository.save(any()))
                .thenReturn(created);

        ItemDto createdDto = service.createItem(1, dto);

        assertEquals(createdDto, dto);
        verify(userRepository, times(1)).getUserById(1);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateItem_withWrongUserId() {
        ItemDto dto = TestingUtils.createItemDto(1);
        Item item = TestingUtils.createItem(1, 1);

        when(itemRepository.getItemById(any()))
                .thenReturn(item);

        AccessViolationException e = assertThrows(
                AccessViolationException.class,
                () -> service.updateItem(2, 1, dto)
        );
        assertEquals(e.getMessage(), "Вы не можете обновлять чужую вещь");
    }

    @Test
    void updateItem_withWrongItemId() {
        when(itemRepository.getItemById(any()))
                .thenReturn(null);

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.updateItem(1, 1, TestingUtils.createItemDto())
        );
        assertEquals(e.getMessage(), "Вещь с id=1 не найдена");
    }

    @Test
    void updateItem_success() {
        ItemDto toUpdateDto = TestingUtils.createItemDto(2);
        toUpdateDto.setId(1);
        toUpdateDto.setAvailable(false);

        Item itemBeforeUpdate = TestingUtils.createItem(1, 1);
        Item itemAfterUpdate = TestingUtils.createItem(1, 1);
        itemAfterUpdate.setName(toUpdateDto.getName());
        itemAfterUpdate.setDescription(toUpdateDto.getDescription());
        itemAfterUpdate.setAvailable(toUpdateDto.getAvailable());

        when(itemRepository.getItemById(1))
                .thenReturn(itemBeforeUpdate);

        when(itemRepository.save(any()))
                .thenReturn(itemAfterUpdate);

        ItemDto updatedDto = service.updateItem(1, 1, toUpdateDto);

        verify(itemRepository, times(1)).getItemById(1);
        verify(itemRepository, times(1)).save(any());

        assertEquals(toUpdateDto, updatedDto);

    }

    @Test
    void getItemById_withWrongUserId() {
        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.getItemById(TestingUtils.INVALID_ID, 1)
        );
        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void getItemById_byOwner() {

        Item item = TestingUtils.createItem(1, 1);
        Booking lastBooking = TestingUtils.createBooking(1, 1, 1);
        Booking nextBooking = TestingUtils.createBooking(2, 2, 1);
        Comment comment = TestingUtils.createComment(1);

        ItemExtendedDto itemExtendedDto = mapper.map(item, ItemExtendedDto.class);
        itemExtendedDto.setNextBooking(mapper.map(nextBooking, BookingSimpleDto.class));
        itemExtendedDto.setLastBooking(mapper.map(lastBooking, BookingSimpleDto.class));
        itemExtendedDto.setComments(List.of(mapper.map(comment, CommentDto.class)));

        when(itemRepository.getItemById(eq(1)))
                .thenReturn(item);
        when(commentRepository.getAllByItemId(eq(1)))
                .thenReturn(List.of(comment));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                eq(1),
                eq(BookingStatus.APPROVED),
                any()))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                eq(1),
                eq(BookingStatus.APPROVED),
                any()))
                .thenReturn(Optional.of(nextBooking));

        ItemExtendedDto result = service.getItemById(1, 1);

        assertEquals(result, itemExtendedDto);

    }

    @Test
    void getItemById_byOtherUser() {

        Item item = TestingUtils.createItem(1, 1);
        Booking lastBooking = TestingUtils.createBooking(1, 1, 1);
        Booking nextBooking = TestingUtils.createBooking(2, 2, 1);
        Comment comment = TestingUtils.createComment(1);

        ItemExtendedDto itemExtendedDto = mapper.map(item, ItemExtendedDto.class);
        itemExtendedDto.setNextBooking(null);
        itemExtendedDto.setLastBooking(null);
        itemExtendedDto.setComments(List.of(mapper.map(comment, CommentDto.class)));

        when(itemRepository.getItemById(eq(1)))
                .thenReturn(item);
        when(commentRepository.getAllByItemId(eq(1)))
                .thenReturn(List.of(comment));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                eq(1),
                eq(BookingStatus.APPROVED),
                any()))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                eq(1),
                eq(BookingStatus.APPROVED),
                any()))
                .thenReturn(Optional.of(nextBooking));

        ItemExtendedDto result = service.getItemById(2, 1);

        assertEquals(result, itemExtendedDto);
        verify(bookingRepository, times(0))
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, times(0))
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(anyInt(), any(), any());
    }


    @Test
    void getAllItems_byOwner() {

        Item item = TestingUtils.createItem(1, 1);
        Booking lastBooking = TestingUtils.createBooking(1, 1, 1);
        Booking nextBooking = TestingUtils.createBooking(2, 2, 1);

        ItemExtendedDto itemExtendedDto = mapper.map(item, ItemExtendedDto.class);
        itemExtendedDto.setNextBooking(mapper.map(nextBooking, BookingSimpleDto.class));
        itemExtendedDto.setLastBooking(mapper.map(lastBooking, BookingSimpleDto.class));

        when(itemRepository.findAllByOwnerIdOrderById(eq(1)))
                .thenReturn(List.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                eq(1),
                eq(BookingStatus.APPROVED),
                any()))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                eq(1),
                eq(BookingStatus.APPROVED),
                any()))
                .thenReturn(Optional.of(nextBooking));

        List<ItemExtendedDto> result = service.getAllItems(1);

        assertEquals(result.get(0), itemExtendedDto);

    }

    @Test
    void search_withBlankQuery() {
        Item item = TestingUtils.createItem(1, 1);

        when(itemRepository.findAvailableItemsByNameOrDescription(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> result = service.search(1, "   ");

        assertEquals(result.size(), 0);
        verify(itemRepository, times(0)).findAvailableItemsByNameOrDescription(anyString());
    }

    @Test
    void search() {
        Item item = TestingUtils.createItem(1, 1);
        ItemDto itemDto = mapper.map(item, ItemDto.class);

        when(itemRepository.findAvailableItemsByNameOrDescription(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> result = service.search(1, "test");

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), itemDto);
        verify(itemRepository, times(1)).findAvailableItemsByNameOrDescription(anyString());
    }

    @Test
    void addComment_withWrongUserId() {

        CommentCreateDto commentCreateDto = TestingUtils.createCommentCreateDto(1);

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.addComment(1, TestingUtils.INVALID_ID, commentCreateDto)
        );
        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void addComment_withWrongItemId() {
        CommentCreateDto commentCreateDto = TestingUtils.createCommentCreateDto(1);

        when(itemRepository.getItemById(any()))
                .thenReturn(null);

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.addComment(1, 1, commentCreateDto)
        );
        assertEquals(e.getMessage(), "Вещь с id=1 не найдена");
    }

    @Test
    void addComment_notByBooker() {
        CommentCreateDto commentCreateDto = TestingUtils.createCommentCreateDto(1);
        Item item = TestingUtils.createItem(1, 1);

        when(itemRepository.getItemById(any()))
                .thenReturn(item);

        when(bookingRepository
                .findFirstByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(anyInt(), anyInt(), any(), any()))
                .thenReturn(Optional.empty());

        DataValidationException e = assertThrows(
                DataValidationException.class,
                () -> service.addComment(1, 1, commentCreateDto)
        );
        assertEquals(e.getMessage(), "Вы не можете оставлять отзыв к этой вещи");
    }

    @Test
    void addComment_success() {
        CommentCreateDto commentCreateDto = TestingUtils.createCommentCreateDto(1);
        Item item = TestingUtils.createItem(1, 1);

        when(itemRepository.getItemById(any()))
                .thenReturn(item);

        when(bookingRepository
                .findFirstByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(anyInt(), anyInt(), any(), any()))
                .thenReturn(Optional.of(TestingUtils.createBooking(1, 1, 1)));

        when(commentRepository.save(any()))
                .thenReturn(mapper.map(commentCreateDto, Comment.class));

        CommentDto result = service.addComment(1, 1, commentCreateDto);

        assertEquals(result.getId(), commentCreateDto.getId());
        assertEquals(result.getText(), commentCreateDto.getText());

        verify(itemRepository, times(1)).getItemById(anyInt());
        verify(bookingRepository, times(1))
                .findFirstByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(anyInt(), anyInt(), any(), any());
        verify(commentRepository, times(1)).save(any());
    }
}