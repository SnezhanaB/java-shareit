package ru.practicum.shareit.utils;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class TestingUtils {
    private TestingUtils() {
    }

    private static final ModelMapper mapper = new ModelMapper();

    public static final String X_USER_HEADER = "X-Sharer-User-Id";

    public static final Integer INVALID_ID = 999;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static final String START_AS_STRING = "2024-05-01T20:00:00";

    public static final String END_AS_STRING = "2024-05-01T22:00:00";

    public static final LocalDateTime START_DATE = LocalDateTime.parse(START_AS_STRING, DATE_TIME_FORMATTER);

    public static final LocalDateTime END_DATE = LocalDateTime.parse(END_AS_STRING, DATE_TIME_FORMATTER);

    /**
     * Получить текущее время с точностью до секунды
     */
    public static LocalDateTime now() {
        return LocalDateTime.parse(LocalDateTime.now().format(DATE_TIME_FORMATTER), DATE_TIME_FORMATTER);
    }

    public static UserDto createUserDto() {
        return createUserDto(1);
    }

    public static UserDto createUserDto(Integer userId) {
        return new UserDto(userId, "User" + userId, "user" + userId + "@example.com");
    }

    public static User createUser(Integer userId) {
        return mapper.map(createUserDto(userId), User.class);
    }

    public static User createUser() {
        return createUser(1);
    }

    public static ItemDto createItemDto() {
      return createItemDto(1);
    }

    public static ItemDto createItemDto(Integer id) {
        return new ItemDto(id, "Дрель " + id, "Аккумуляторная " + id, true, 1);
    }

    public static ItemExtendedDto createItemExtendedDto() {
        ItemExtendedDto dto = mapper.map(createItemDto(), ItemExtendedDto.class);
        dto.setComments(List.of(createCommentDto()));
        dto.setNextBooking(createBookingSimpleDto());
        dto.getNextBooking().setId(2);
        dto.setLastBooking(createBookingSimpleDto());
        return dto;
    }

    public static Item createItem(Integer itemId, Integer userId) {
        Item item = mapper.map(createItemDto(itemId), Item.class);
        item.setOwner(TestingUtils.createUser(userId));
        return item;
    }

    public static Item createItem() {
        return createItem(1, 1);
    }


    public static BookingDto createBookingDto() {
        return createBookingDto(1);
    }

    public static BookingDto createBookingDto(Integer bookingId) {
        return createBookingDto(bookingId, 1, 1);
    }

    public static BookingDto createBookingDto(Integer bookingId, Integer itemId, Integer userId) {
        LocalDateTime now = now();
        return new BookingDto(bookingId, now, now.plusDays(1), createItemDto(itemId), createUserDto(userId),
                BookingStatus.WAITING);
    }

    public static Booking createBooking(Integer bookingId, Integer itemId, Integer userId) {
        return mapper.map(createBookingDto(bookingId, itemId, userId), Booking.class);
    }

    public static BookingSimpleDto createBookingSimpleDto() {
        return mapper.map(createBookingDto(), BookingSimpleDto.class);
    }

    public static BookingCreateDto createBookingCreateDto() {
        return mapper.map(createBookingDto(), BookingCreateDto.class);
    }

    public static CommentDto createCommentDto(Integer commentId) {
        return new CommentDto(commentId, "Text" + commentId, "AuthorName" + commentId, START_DATE);
    }

    public static CommentDto createCommentDto() {
        return createCommentDto(1);
    }

    public static CommentCreateDto createCommentCreateDto(Integer commentId) {
        return mapper.map(createCommentDto(commentId), CommentCreateDto.class);
    }

    public static Comment createComment(Integer commentId) {
        return mapper.map(createCommentDto(commentId), Comment.class);
    }

    public static ItemRequestDto createItemRequestDto(Integer requestId, Integer requesterId, Integer itemId) {
        ItemRequestDto dto = mapper.map(createItemRequestCreateDto(requesterId), ItemRequestDto.class);
        dto.setId(requestId);
        dto.setItems(List.of(createItemDto(itemId)));
        dto.setCreated(now());
        return dto;
    }

    public static ItemRequestDto createItemRequestDto() {
        return createItemRequestDto(1, 1, 1);
    }

    public static ItemRequestCreateDto createItemRequestCreateDto(Integer requesterId) {
        return new ItemRequestCreateDto("Ищу отвертку, user: " + requesterId, requesterId);
    }

    public static ItemRequest createItemRequest(Integer requestId, Integer requesterId) {
        return mapper.map(createItemRequestDto(requestId, requesterId, 1), ItemRequest.class);
    }

}
