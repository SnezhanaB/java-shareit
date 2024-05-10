package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.TestingUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private final ModelMapper mapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        // mocking userRepository
        User user1 = TestingUtils.createUser(1);
        when(userRepository.findById(eq(1)))
                .thenReturn(Optional.of(user1));

        when(userRepository.findById(eq(TestingUtils.INVALID_ID)))
                .thenReturn(Optional.empty());
    }

    @Test
    void create_withWrongUserId() {
        ItemRequestCreateDto createDto = TestingUtils.createItemRequestCreateDto(TestingUtils.INVALID_ID);

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.create(createDto)
        );
        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void create() {
        ItemRequestCreateDto createDto = TestingUtils.createItemRequestCreateDto(1);
        ItemRequest request = TestingUtils.createItemRequest(1, 1);
        ItemRequestDto expected = mapper.map(request, ItemRequestDto.class);

        when(itemRequestRepository.save(any()))
                .thenReturn(request);

        ItemRequestDto created = service.create(createDto);
        assertEquals(expected, created);
    }

    @Test
    void getById_withWrongUserId() {

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.getById(TestingUtils.INVALID_ID, 1)
        );
        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void getById_withWrongRequestId() {

        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.getById(1, 1)
        );
        assertEquals(e.getMessage(), "Запрос с id=1 не найден");
    }

    @Test
    void getById() {
        ItemRequest request = TestingUtils.createItemRequest(1, 1);
        ItemRequestDto expected = TestingUtils.createItemRequestDto(1, 1, 1);
        Item item = TestingUtils.createItem(1, 1);

        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(request));

        when(itemRepository.getItemsByRequestId(anyInt()))
                .thenReturn(List.of(item));

        ItemRequestDto result = service.getById(1, 1);
        assertEquals(expected, result);

    }

    @Test
    void getByRequesterId_withWrongUserId() {

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.getByRequesterId(TestingUtils.INVALID_ID)
        );
        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void getByRequesterId() {
        ItemRequest request = TestingUtils.createItemRequest(1, 1);
        List<ItemRequestDto> expected = List.of(TestingUtils.createItemRequestDto(1, 1, 1));
        Item item = TestingUtils.createItem(1, 1);

        when(itemRequestRepository.findAllByRequesterId(anyInt(), any()))
                .thenReturn(List.of(request));

        when(itemRepository.getItemsByRequestId(anyInt()))
                .thenReturn(List.of(item));

        List<ItemRequestDto> result = service.getByRequesterId(1);
        assertEquals(expected, result);
    }

    @Test
    void getRequests_withWrongUserId() {

        NotFoundException e = assertThrows(
                NotFoundException.class,
                () -> service.getRequests(TestingUtils.INVALID_ID, 0, 10));

        assertEquals(e.getMessage(), "Пользователь с id=" + TestingUtils.INVALID_ID + " не найден");
    }

    @Test
    void getRequests() {
        ItemRequest request = TestingUtils.createItemRequest(1, 1);
        List<ItemRequestDto> expected = List.of(TestingUtils.createItemRequestDto(1, 1, 1));
        Item item = TestingUtils.createItem(1, 1);

        when(itemRequestRepository.findAllByRequesterIdNot(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(request)));

        when(itemRepository.getItemsByRequestId(anyInt()))
                .thenReturn(List.of(item));

        List<ItemRequestDto> result = service.getRequests(1, 0, 10);
        assertEquals(expected, result);
    }
}