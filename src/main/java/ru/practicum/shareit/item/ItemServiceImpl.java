package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Primary
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public ItemDto createItem(Integer userId, ItemDto itemDto) {

        if (itemDto.getAvailable() == null) {
            throw new DataValidationException("Не заполнено поле 'available'");
        }

        User owner = userRepository.getUserById(userId);
        if (owner == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        Item toCreate = mapper.map(itemDto, Item.class);
        toCreate.setOwner(owner);

        Item created = itemRepository.save(toCreate);

        return mapper.map(created, ItemDto.class);
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        Item updated = itemRepository.getItemById(itemId);

        if (updated == null) {
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена");
        }

        if (!userId.equals(updated.getOwner().getId())) {
            throw new AccessViolationException("Вы не можете обновлять чужую вещь");
        }

        if (itemDto.getAvailable() != null) {
            updated.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            updated.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            updated.setDescription(itemDto.getDescription());
        }

        return mapper.map(itemRepository.save(updated), ItemDto.class);
    }

    @Override
    public ItemExtendedDto getItemById(Integer userId, Integer itemId) {

        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        Timestamp now = Timestamp.from(Instant.now());

        Item item = itemRepository.getItemById(itemId);
        ItemExtendedDto itemDto = mapper.map(item, ItemExtendedDto.class);

        // Если запрос от владельца вещи, то заполняем lastBooking и nextBooking
        if (userId.equals(item.getOwner().getId())) {
            Optional<Booking> lastBooking =
                    bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                            itemId,
                            BookingStatus.APPROVED,
                            now);
            lastBooking.ifPresent(booking -> itemDto.setLastBooking(mapper.map(booking, BookingSimpleDto.class)));

            Optional<Booking> nextBooking =
                    bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            itemId,
                            BookingStatus.APPROVED,
                            now);
            nextBooking.ifPresent(booking -> itemDto.setNextBooking(mapper.map(booking, BookingSimpleDto.class)));
        }

        itemDto.setComments(
                commentRepository.getAllByItemId(itemId).stream()
                        .map((comment) -> mapper.map(comment,CommentDto.class))
                        .collect(Collectors.toList())
        );

        return itemDto;
    }

    @Override
    public List<ItemExtendedDto> getAllItems(Integer userId) {
        Timestamp now = Timestamp.from(Instant.now());

        return itemRepository.findAllByOwnerIdOrderById(userId).stream()
                .map((i) -> mapper.map(i, ItemExtendedDto.class))
                .map((dto) -> {
                    Optional<Booking> lastBooking =
                            bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                                    dto.getId(),
                                    BookingStatus.APPROVED,
                                    now);

                    lastBooking.ifPresent(booking -> dto.setLastBooking(mapper.map(booking, BookingSimpleDto.class)));

                    Optional<Booking> nextBooking =
                            bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                                    dto.getId(),
                                    BookingStatus.APPROVED,
                                    now);

                    nextBooking.ifPresent(booking -> dto.setNextBooking(mapper.map(booking, BookingSimpleDto.class)));

                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Integer userId, String query) {
        if (query.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findAvailableItemsByNameOrDescription(query).stream()
                .map((i) -> mapper.map(i, ItemDto.class)).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Integer itemId, Integer userId, CommentCreateDto commentCreateDto) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        Item item = itemRepository.getItemById(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь с id=" + userId + " не найдена");
        }

        if (bookingRepository.findFirstByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                userId, itemId, BookingStatus.APPROVED, Timestamp.from(Instant.now())).isEmpty()) {
            throw new DataValidationException("Вы не можете оставлять отзыв к этой вещи");
        }

        Comment newComment = mapper.map(commentCreateDto, Comment.class);
        newComment.setItem(item);
        newComment.setAuthor(user);
        newComment.setCreated(Timestamp.from(Instant.now()));
        commentRepository.save(newComment);

        return mapper.map(newComment, CommentDto.class);
    }
}
