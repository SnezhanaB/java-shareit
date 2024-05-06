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
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.TestingUtils;

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
    void updateItem() {
    }

    @Test
    void getItemById() {
    }

    @Test
    void getAllItems() {
    }

    @Test
    void search() {
    }

    @Test
    void addComment() {
    }
}